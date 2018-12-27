package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.INVALID_PROOF_SIGNATURE;

public class ProofSignatureVerificationException extends DIDResolverException {

  private static final String ERROR_MESSAGE = "Failed to verify signature of the proof for DDO with DID - ";

  public ProofSignatureVerificationException(String did) {
    super(ERROR_MESSAGE + did, INVALID_PROOF_SIGNATURE);
  }

  public ProofSignatureVerificationException(String did, Throwable cause) {
    super(ERROR_MESSAGE + did, cause, INVALID_PROOF_SIGNATURE);
  }
}
