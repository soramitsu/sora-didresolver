package jp.co.soramitsu.sora.didresolver.exceptions;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(UNAUTHORIZED)
public class BadProofException extends RuntimeException {

  public BadProofException(String did) {
    super("Failed to verify proof for DDO with DID: " + did);
  }
}
