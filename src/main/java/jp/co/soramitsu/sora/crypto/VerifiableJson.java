package jp.co.soramitsu.sora.crypto;

import java.util.List;

public interface VerifiableJson extends MapSerializable {

  void setProof(List<ProofProxy> o);

  List<ProofProxy> getProof();
}
