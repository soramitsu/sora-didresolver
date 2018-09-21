package jp.co.soramitsu.sora.didresolver.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class PublicKeyValueNotPresentedException extends RuntimeException {

  public PublicKeyValueNotPresentedException(String did) {
    super("Failed to verify proof for DDO with DID = '" + did
        + "' due to absence of a Public Key Value");
  }
}
