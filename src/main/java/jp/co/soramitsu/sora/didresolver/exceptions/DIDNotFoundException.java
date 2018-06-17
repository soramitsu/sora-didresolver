package jp.co.soramitsu.sora.didresolver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DIDNotFoundException extends RuntimeException {

  public DIDNotFoundException(String did) {
    super("could not find did '" + did + "'.");
  }
}
