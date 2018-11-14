package jp.co.soramitsu.sora.didresolver.exceptions;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class IrohaTransactionCommitmentException extends RuntimeException {

  public IrohaTransactionCommitmentException(String txHash, Throwable e) {
    super("Cannot commit transaction with hash: " + txHash, e);
  }
}
