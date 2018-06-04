package jp.co.soramitsu.sora.crypto;

import java.time.Instant;

public interface ProofProxy {

  Instant getCreated();

  void setCreated(Instant dateTime);

  void setSignatureValue(byte[] o);

  byte[] getSignatureValue();

  String getType();

}
