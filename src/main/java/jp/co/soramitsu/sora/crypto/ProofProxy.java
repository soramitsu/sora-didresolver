package jp.co.soramitsu.sora.crypto;

import java.time.Instant;

public interface ProofProxy {

  Instant getCreated();

  byte[] getSignatureValue();

  String getType();

  void setCreated(Instant dateTime);

  void setSignatureValue(byte[] o);

  void setType(String type);

}
