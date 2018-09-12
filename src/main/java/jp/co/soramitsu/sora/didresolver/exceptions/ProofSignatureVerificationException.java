package jp.co.soramitsu.sora.didresolver.exceptions;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(UNPROCESSABLE_ENTITY)
public class ProofSignatureVerificationException extends RuntimeException {

  public ProofSignatureVerificationException(String did, Throwable cause) {
    super("Failed to verify signature of the proof for DDO with DID = " + did, cause);
  }
}
