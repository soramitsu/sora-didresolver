package jp.co.soramitsu.sora.crypto.algorithms;

import java.security.KeyPair;
import java.security.PublicKey;

public interface RawSignatureStrategy {

  byte[] rawSign(byte[] data, KeyPair keypair) throws SignatureSuiteException;

  boolean rawVerify(byte[] data, byte[] signature, PublicKey publicKey)
      throws SignatureSuiteException;

  KeyPair generateKeypair() throws SignatureSuiteException;

  String getType();


  class SignatureSuiteException extends Exception {

    public SignatureSuiteException(String msg) {
      super(msg);
    }

    public SignatureSuiteException(Exception e) {
      super(e);
    }
  }
}
