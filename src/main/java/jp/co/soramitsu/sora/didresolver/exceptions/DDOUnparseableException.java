package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.DID_UNPARSEABLE;

public class DDOUnparseableException extends DIDResolverException {

  public DDOUnparseableException(Exception e) {
    super("DDO can't be parsed, message: " + e.getMessage(), DID_UNPARSEABLE);
  }
}
