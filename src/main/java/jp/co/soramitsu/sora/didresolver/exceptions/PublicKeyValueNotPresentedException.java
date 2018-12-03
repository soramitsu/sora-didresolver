package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.PUBLIC_KEY_VALUE_NOT_PRESENTED;

public class PublicKeyValueNotPresentedException extends DIDResolverException {

  public PublicKeyValueNotPresentedException(String did) {
    super("Failed to verify proof for DDO with DID - '" + did
        + "' due to absence of a Public Key Value", PUBLIC_KEY_VALUE_NOT_PRESENTED);
  }
}
