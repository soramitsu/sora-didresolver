package jp.co.soramitsu.sora.didresolver.exceptions;

import jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode;
import lombok.Getter;

public class DIDResolverException extends Exception {

  @Getter
  private final ResponseCode responseCode;

  public DIDResolverException(String message, ResponseCode responseCode) {
    this(message, null, responseCode);
  }

  public DIDResolverException(String message, Throwable cause, ResponseCode responseCode) {
    super(message, cause);
    this.responseCode = responseCode;
  }
}
