package jp.co.soramitsu.sora.didresolver.exceptions;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.DID_UNPARSEABLE;

import java.io.IOException;

public class DDOUnparseableException extends DIDResolverException {

  public DDOUnparseableException(IOException e) {
    super("DDO can't be parsed, message: " + e.getMessage(), DID_UNPARSEABLE);
  }
}
