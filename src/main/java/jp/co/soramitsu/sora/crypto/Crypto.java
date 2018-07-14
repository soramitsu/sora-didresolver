package jp.co.soramitsu.sora.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.hash.RawDigestStrategy;
import jp.co.soramitsu.sora.util.bencoder.BencodeMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Crypto {

  @Setter
  @Getter
  private RawDigestStrategy digestStrategy;

  @Getter
  @Setter
  private ObjectMapper mapper;

  public Crypto(@NotNull RawDigestStrategy digestStrategy) {
    this(digestStrategy, new BencodeMapper());
  }

  public Crypto(@NotNull RawDigestStrategy digestStrategy, @NotNull ObjectMapper mapper) {
    this.digestStrategy = digestStrategy;
    this.mapper = mapper;
    log.debug("created new Crypto object with digest strategy - {}", digestStrategy);
  }

  private byte[] createVerifyHash(VerifiableJson document, ProofProxy proof)
      throws CreateVerifyHashException {
    log.info("verify hash for document");
    // sanitize inputs
    sanitizeDocument(document);
    sanitizeProof(proof);
    log.debug("encode input into a string");
    // encode input into a string
    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      mapper.writeValue(stream, document);
      mapper.writeValue(stream, proof);
      // calculate digest(document + proof)
      byte[] hash = digestStrategy.digest(stream.toByteArray());
      log.debug("calculate digest(document + proof) - {}", new String(hash));
      return hash;
    } catch (IOException e) {
      throw new CreateVerifyHashException(e);
    }

  }

  private void sanitizeDocument(VerifiableJson document) {
    log.debug("sanitize document for verify hash");
    document.setProof(null);
  }

  private void sanitizeProof(ProofProxy proof) {
    log.debug("sanitize proof for verify hash");
    // if "created" is empty, then set current time as "created"
    if (proof.getCreated() == null) {
      log.debug("set current time {} as \"created\"", Instant.now());
      proof.setCreated(Instant.now());
    }

    proof.setSignatureValue(null);
  }

  public void sign(VerifiableJson document, KeyPair keypair, ProofProxy proof)
      throws CreateVerifyHashException, SignatureSuiteException, NoSuchStrategy {
    log.info("sign document");
    RawSignatureStrategy signer = getSignatureStrategy(proof.getType());
    log.debug("recieved signature strategy for sign document - {}", signer);
    // backup proofs
    List<ProofProxy> proofs = document.getProof();

    // hash and sign input
    byte[] hash = createVerifyHash(document, proof);
    byte[] signature = signer.rawSign(hash, keypair);
    log.debug("include signature into a proof, add it to saved proofs");
    // include signature into a proof, add it to saved proofs
    proof.setSignatureValue(signature);
    if (proofs == null) {
      proofs = new ArrayList<>();
    }
    proofs.add(proof);
    log.debug("add all {} proofs to the document", proofs.size());
    // add all proofs to the document
    document.setProof(proofs);
  }

  /**
   * Verify specific proof
   *
   * @return true if proof is valid, false otherwise
   */
  public boolean verify(VerifiableJson document, PublicKey publicKey, ProofProxy proof)
      throws CreateVerifyHashException, SignatureSuiteException, NoSuchStrategy {
    log.info("verify specific proof");
    RawSignatureStrategy verifier = getSignatureStrategy(proof.getType());
    log.debug("recieved verify strategy for document - {}", verifier);
    byte[] signature = proof.getSignatureValue();
    byte[] hash = createVerifyHash(document, proof);

    return verifier.rawVerify(hash, signature, publicKey);
  }

  /**
   * Verify all proofs
   *
   * @return true if all proofs are valid, false otherwise
   */
  public boolean verifyAll(VerifiableJson document, PublicKey publicKey)
      throws CreateVerifyHashException, SignatureSuiteException, NoSuchStrategy {
    log.info("verify all proofs of document");
    final List<ProofProxy> proofs = document.getProof();
    if (proofs == null || proofs.isEmpty()) {
      log.error("document has no verifiable proofs");
      throw new SignatureSuiteException("document has no verifiable proofs");
    }

    for (ProofProxy proof : proofs) {
      if (!verify(document, publicKey, proof)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Create instance of signature strategy by name. The signature strategy should be in the same directory as RawSignatureStrategy interface.
   *
   * @param signatureStrategyName - signature strategy name
   * @return instance of signature strategy which implements interface RawSignatureStrategy
   * @throws NoSuchStrategy - when signature strategy with name signatureStrategyName does not found
   */
  public RawSignatureStrategy getSignatureStrategy(String signatureStrategyName)
      throws NoSuchStrategy {
    try {
      return (RawSignatureStrategy) Class.forName(RawSignatureStrategy.class.getPackage().getName()+ '.' + signatureStrategyName).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new NoSuchStrategy(signatureStrategyName);
    }
  }

  public static class CreateVerifyHashException extends IOException {

    CreateVerifyHashException(IOException e) {
      super(e);
    }
  }

  public static class NoSuchStrategy extends Exception {

    public NoSuchStrategy(String key) {
      super(key + " signature suite is not implemented");
    }
  }
}
