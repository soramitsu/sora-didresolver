package jp.co.soramitsu.sora.didresolver.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class InvalidProofException extends RuntimeException {

  public InvalidProofException(String did) {
    super("Invalid proof in DDO with DID " + did);
  }

}
