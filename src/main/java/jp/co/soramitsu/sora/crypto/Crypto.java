package jp.co.soramitsu.sora.crypto;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.algorithms.SignatureSuiteRegistry;
import jp.co.soramitsu.sora.crypto.hash.RawDigestStrategy;
import jp.co.soramitsu.sora.util.Bencoder;
import lombok.Getter;
import lombok.Setter;


public class Crypto {

  @Setter
  @Getter
  private RawDigestStrategy digestStrategy;

  public Crypto(@NotNull RawDigestStrategy digestStrategy) {
    this.digestStrategy = digestStrategy;
  }

  private byte[] createVerifyHash(VerifiableJson document, ProofProxy proof)
      throws CreateVerifyHashException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Bencoder bencoder = new Bencoder(UTF_8, os);

    // sanitize inputs
    this.sanitizeDocument(document);
    this.sanitizeProof(proof);

    // encode input into a string
    try {
      bencoder.encode(document.serializeAsMap());
      bencoder.encode(proof.serializeAsMap());
    } catch (IOException e) {
      throw new CreateVerifyHashException(e);
    }

    return digestStrategy.digest(os.toByteArray());
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

  private RawSignatureStrategy getSuite(String type) throws InvalidAlgorithmException {
    // find appropriate digital signature algorithm
    if (!SignatureSuiteRegistry.has(type)) {
      throw new InvalidAlgorithmException(type + " signature suite is not implemented");
    }

    return SignatureSuiteRegistry.get(type);
  }

  public void sign(VerifiableJson document, KeyPair keypair, ProofProxy proof)
      throws InvalidAlgorithmException, CreateVerifyHashException, SignatureSuiteException {
    RawSignatureStrategy signer = getSuite(proof.getType());

    // backup proofs
    List<ProofProxy> proofs = document.getProof();

    // hash and sign input
    byte[] hash = createVerifyHash(document, proof);
    byte[] signature = signer.rawSign(hash, keypair);

    // include signature into a proof, add it to saved proofs
    proof.setSignatureValue(signature);
    if (proofs == null) {
      proofs = new LinkedList<>();
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
      throws InvalidAlgorithmException, CreateVerifyHashException, SignatureSuiteException {
    RawSignatureStrategy verifier = getSuite(proof.getType());

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
      throws CreateVerifyHashException, InvalidAlgorithmException, SignatureSuiteException {
    final List<ProofProxy> proofs = document.getProof();
    if (proofs == null || proofs.isEmpty()) {
      throw new SignatureSuiteException("document has no verifiable proofs");
    }

    for (ProofProxy proof : proofs) {
      if (!this.verify(document, publicKey, proof)) {
        return false;
      }
    }

    return true;
  }

  public static class InvalidAlgorithmException extends Exception {

    public InvalidAlgorithmException(String message) {
      super(message);
    }
  }

  public static class CreateVerifyHashException extends IOException {

    CreateVerifyHashException(IOException e) {
      super(e);
    }
  }
}
