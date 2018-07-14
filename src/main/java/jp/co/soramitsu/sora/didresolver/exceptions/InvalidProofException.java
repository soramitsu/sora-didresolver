package jp.co.soramitsu.sora.didresolver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProofException extends RuntimeException {

  public InvalidProofException(String did) {
    super("Invalid proof in DDO with DID " + did);
  }

}
