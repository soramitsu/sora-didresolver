package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.DID_DUPLICATE;

public class DIDDuplicateException extends DIDResolverException {

  public DIDDuplicateException(String did) {
    super("DID " + did + " has already registered", DID_DUPLICATE);
  }
}
