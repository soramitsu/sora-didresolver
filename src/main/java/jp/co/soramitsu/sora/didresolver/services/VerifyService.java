package jp.co.soramitsu.sora.didresolver.services;

import java.util.List;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;

public interface VerifyService {

  /**
   * Method for verifying proof of DDO
   *
   * @param ddo - valid DDO
   * @param publicKeyBytes - public key for verifying DDO in bytes
   * @return true if DDO proof valid, otherwise return false
   */
  boolean verifyDDOProof(DDO ddo, byte[] publicKeyBytes);

  /**
   * Receiving the public key specified in the proof
   *
   * @param proof an element of the type of Proof for which the corresponding key will be searched
   * @param publicKeys collection of public keys of document
   * @return public key corresponding to the transferred proof
   */
  Optional<PublicKey> getPublicKeyByProof(Proof proof, List<PublicKey> publicKeys);
}
