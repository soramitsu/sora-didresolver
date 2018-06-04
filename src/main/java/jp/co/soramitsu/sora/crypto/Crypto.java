package jp.co.soramitsu.sora.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.algorithms.SignatureSuiteRegistry;
import jp.co.soramitsu.sora.crypto.algorithms.SignatureSuiteRegistry.InvalidAlgorithmException;
import jp.co.soramitsu.sora.crypto.hash.RawDigestStrategy;
import jp.co.soramitsu.sora.util.bencoder.BencodeMapper;
import lombok.Getter;
import lombok.Setter;


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
  }

  private byte[] createVerifyHash(VerifiableJson document, ProofProxy proof)
      throws CreateVerifyHashException {
    // sanitize inputs
    sanitizeDocument(document);
    sanitizeProof(proof);

    // encode input into a string
    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      mapper.writeValue(stream, document);
      mapper.writeValue(stream, proof);

      // calculate digest(document + proof)
      return digestStrategy.digest(stream.toByteArray());
    } catch (IOException e) {
      throw new CreateVerifyHashException(e);
    }

  }

  private void sanitizeDocument(VerifiableJson document) {
    document.setProof(null);
  }

  private void sanitizeProof(ProofProxy proof) {
    // if "created" is empty, then set current time as "created"
    if (proof.getCreated() == null) {
      proof.setCreated(Instant.now());
    }

    proof.setSignatureValue(null);
  }

  public void sign(VerifiableJson document, KeyPair keypair, ProofProxy proof)
      throws CreateVerifyHashException, SignatureSuiteException, InvalidAlgorithmException {
    RawSignatureStrategy signer = SignatureSuiteRegistry.get(proof.getType());

    // backup proofs
    List<ProofProxy> proofs = document.getProof();

    // hash and sign input
    byte[] hash = createVerifyHash(document, proof);
    byte[] signature = signer.rawSign(hash, keypair);

    // include signature into a proof, add it to saved proofs
    proof.setSignatureValue(signature);
    if (proofs == null) {
      proofs = new ArrayList<>();
    }
    proofs.add(proof);

    // add all proofs to the document
    document.setProof(proofs);
  }

  /**
   * Verify specific proof
   *
   * @return true if proof is valid, false otherwise
   */
  public boolean verify(VerifiableJson document, PublicKey publicKey, ProofProxy proof)
      throws CreateVerifyHashException, SignatureSuiteException, InvalidAlgorithmException {
    RawSignatureStrategy verifier = SignatureSuiteRegistry.get(proof.getType());

    byte[] hash = createVerifyHash(document, proof);
    byte[] signature = proof.getSignatureValue();

    return verifier.rawVerify(hash, signature, publicKey);
  }

  /**
   * Verify all proofs
   *
   * @return true if all proofs are valid, false otherwise
   */
  public boolean verifyAll(VerifiableJson document, PublicKey publicKey)
      throws CreateVerifyHashException, SignatureSuiteException, InvalidAlgorithmException {
    final List<ProofProxy> proofs = document.getProof();
    if (proofs == null || proofs.isEmpty()) {
      throw new SignatureSuiteException("document has no verifiable proofs");
    }

    for (ProofProxy proof : proofs) {
      if (!verify(document, publicKey, proof)) {
        return false;
      }
    }

    return true;
  }


  public static class CreateVerifyHashException extends IOException {

    CreateVerifyHashException(IOException e) {
      super(e);
    }
  }
}
