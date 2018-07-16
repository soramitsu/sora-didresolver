package jp.co.soramitsu.sora.crypto;

public interface VerifiableJson<T extends ProofProxy> {

  void setProof(T o);

  T getProof();
}
