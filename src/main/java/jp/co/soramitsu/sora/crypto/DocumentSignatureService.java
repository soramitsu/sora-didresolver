package jp.co.soramitsu.sora.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.hash.RawDigestStrategy;
import jp.co.soramitsu.sora.util.bencoder.BencodeMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentSignatureService {

  @Setter
  @Getter
  private RawDigestStrategy digestStrategy;

  @Getter
  @Setter
  private ObjectMapper mapper;

  public DocumentSignatureService(@NotNull RawDigestStrategy digestStrategy) {
    this(digestStrategy, new BencodeMapper());
  }

  public DocumentSignatureService(@NotNull RawDigestStrategy digestStrategy,
      @NotNull ObjectMapper mapper) {
    this.digestStrategy = digestStrategy;
    this.mapper = mapper;
    log.debug("created new DocumentSignatureService object with digest strategy - {}",
        digestStrategy);
  }

  private byte[] createVerifyHash(VerifiableJson document, ProofProxy proof)
      throws CreateVerifyHashException {
    log.debug("verify hash for document");
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
    log.debug("sign document");
    RawSignatureStrategy signer = getSignatureStrategy(proof.getType());
    log.debug("received signature strategy for sign document - {}", signer);
    byte[] hash = createVerifyHash(document, proof);
    byte[] signature = signer.rawSign(hash, keypair);
    proof.setSignatureValue(signature);
    log.debug("include signature into a proof and add it to document");
    document.setProof(proof);
  }

  /**
   * Verify specific proof
   *
   * @return true if proof is valid, false otherwise
   */
  public boolean verify(VerifiableJson document, PublicKey publicKey, ProofProxy proof)
      throws CreateVerifyHashException, SignatureSuiteException, NoSuchStrategy {
    log.debug("verify specific proof");
    RawSignatureStrategy verifier = getSignatureStrategy(proof.getType());
    log.debug("received verify strategy for document - {}", verifier);
    byte[] signature = proof.getSignatureValue();
    byte[] hash = createVerifyHash(document, proof);

    return verifier.rawVerify(hash, signature, publicKey);
  }

  /**
   * Create instance of signature strategy by name. The signature strategy should be in the same
   * directory as RawSignatureStrategy interface.
   *
   * @param signatureStrategyName - signature strategy name
   * @return instance of signature strategy which implements interface RawSignatureStrategy
   * @throws NoSuchStrategy - when signature strategy with name signatureStrategyName does not
   * found
   */
  public RawSignatureStrategy getSignatureStrategy(String signatureStrategyName)
      throws NoSuchStrategy {
    try {
      return (RawSignatureStrategy) Class
          .forName(RawSignatureStrategy.class.getPackage().getName() + '.' + signatureStrategyName)
          .newInstance();
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
