package jp.co.soramitsu.sora.didresolver.exceptions;

public class DIDUnparseableException extends UnparseableException {

  public DIDUnparseableException(String did) {
    super("Failed to parse did: " + did);
  }
}
