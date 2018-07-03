package jp.co.soramitsu.sora.didresolver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BadProofException extends RuntimeException {

  public BadProofException(String message) {
    super(message);
  }
}
