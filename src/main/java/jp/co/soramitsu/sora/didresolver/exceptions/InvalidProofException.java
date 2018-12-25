package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.INVALID_PROOF;

public class InvalidProofException extends DIDResolverException {

  public InvalidProofException(String did) {
    super("Invalid proof in DDO with DID " + did, INVALID_PROOF);
  }
}
