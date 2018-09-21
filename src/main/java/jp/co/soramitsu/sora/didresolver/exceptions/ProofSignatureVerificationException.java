package jp.co.soramitsu.sora.didresolver.exceptions;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(UNPROCESSABLE_ENTITY)
public class ProofSignatureVerificationException extends RuntimeException {

  private static final String ERROR_MESSAGE = "Failed to verify signature of the proof for DDO with DID = ";

  public ProofSignatureVerificationException(String did) {
    super(ERROR_MESSAGE + did);
  }

  public ProofSignatureVerificationException(String did, Throwable cause) {
    super(ERROR_MESSAGE + did, cause);
  }
}
