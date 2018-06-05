package jp.co.soramitsu.sora.didresolver.exceptions;

public class IrohaIdentifierUnparseableException extends UnparseableException {

  public IrohaIdentifierUnparseableException(String identifier) {
    super("Can't parse identifier: " + identifier);
  }

}
