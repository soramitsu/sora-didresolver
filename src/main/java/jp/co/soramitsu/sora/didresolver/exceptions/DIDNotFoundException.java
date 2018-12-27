package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.DID_NOT_FOUND;

public class DIDNotFoundException extends DIDResolverException {

  public DIDNotFoundException(String did) {
    super("Could not find did '" + did + "'.", DID_NOT_FOUND);
  }
}
