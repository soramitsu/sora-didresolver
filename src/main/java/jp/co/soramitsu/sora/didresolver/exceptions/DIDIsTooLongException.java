package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.commons.CommonsConst.MAX_IROHA_KEY_LENGTH;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.DID_IS_TOO_LONG;

public class DIDIsTooLongException extends DIDResolverException {

  public DIDIsTooLongException(String did) {
    super("DID " + did + " is too long. Iroha may accept key only with length smaller than "
        + MAX_IROHA_KEY_LENGTH, DID_IS_TOO_LONG);
  }
}
