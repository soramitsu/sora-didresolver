package jp.co.soramitsu.sora.didresolver.exceptions;

import java.io.IOException;

public class DDOUnparseableException extends UnparseableException {

  public DDOUnparseableException(IOException e) {
    super("DDO can't be parsed, message: " + e.getMessage());
  }
}
