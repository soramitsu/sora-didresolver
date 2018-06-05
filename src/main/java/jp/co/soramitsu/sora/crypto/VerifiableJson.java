package jp.co.soramitsu.sora.crypto;

import java.util.List;

public interface VerifiableJson {

  void setProof(List<ProofProxy> o);

  List<ProofProxy> getProof();
}
