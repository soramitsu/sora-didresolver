package jp.co.soramitsu.sora.didresolver.services;

import java.util.List;
import java.util.Optional;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;

public interface  VerifyService {

  /**
   * Method for verifying proof of DDO
   *
   * @param ddo - valid DDO
   * @param publicKey - public key for verifying DDO
   * @return true if DDO proof valid, otherwise return false
   */
  boolean verifyDDOProof(DDO ddo, String publicKey);

  /**
   * Receiving the public key specified in the proof
   *
   * @param publicKeys collection of public keys of document
   * @param proof an element of the type of Proof for which the corresponding key will be searched
   * @return public key corresponding to the transferred proof
   */
  Optional<PublicKey> getProofPublicKeyByProof(List<PublicKey> publicKeys, Proof proof);
}
