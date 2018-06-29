package jp.co.soramitsu.sora.crypto;

import java.util.List;

public interface VerifiableJson<T extends ProofProxy> {

  void setProof(List<T> o);

  List<T> getProof();
}
