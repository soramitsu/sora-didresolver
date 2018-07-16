package jp.co.soramitsu.sora.didresolver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectUpdateException extends RuntimeException {

  public IncorrectUpdateException(String createdTime, String updatedTime) {
    super("Updated property value " + updatedTime + " MUST be with time more than created - "
        + createdTime);
  }
}
