package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.INCORRECT_UPDATE_TIME;

import jp.co.soramitsu.sora.sdk.did.model.dto.DID;

public class IncorrectUpdateException extends DIDResolverException {

  public IncorrectUpdateException(DID did, String createdTime, String updatedTime) {
    super("Updated property value " + updatedTime + " MUST be with time more than created - "
        + createdTime + " for DDO with DID - " + did, INCORRECT_UPDATE_TIME);
  }
}
