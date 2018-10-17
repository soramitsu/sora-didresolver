package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.commons.CommonsConst.MAX_IROHA_KEY_LENGTH;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class DIDIsTooLongException extends RuntimeException {

  public DIDIsTooLongException(String did) {
    super("DID " + did + " is too long. Iroha may accept key only with length smaller than " + MAX_IROHA_KEY_LENGTH);
  }
}
