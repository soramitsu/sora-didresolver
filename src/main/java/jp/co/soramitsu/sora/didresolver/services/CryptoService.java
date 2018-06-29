package jp.co.soramitsu.sora.didresolver.services;

import java.util.List;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;

public interface CryptoService {

  /**
   * Method for verifying proof of DDO
   *
   * @param ddo - valid DDO
   * @param publicKeyBytes - public key for verifying DDO in bytes
   * @return true if DDO proof valid, otherwise return false
   */
  boolean verifyDDOProof(DDO ddo, byte[] publicKeyBytes);

  /**
   * Method for checking proof correctness
   *
   * @param proof element of type Proof for check
   * @param did valid DID subject
   * @param publicKeys collection of public keys of document
   * @return true if proof correct otherwise return false
   */
  boolean checkProofCorrectness(Proof proof, String did, List<PublicKey> publicKeys);

  /**
   * Receiving the public key specified in the proof
   *
   * @param proof an element of the type of Proof for which the corresponding key will be searched
   * @param publicKeys collection of public keys of document
   * @return public key corresponding to the transferred proof
   */
  PublicKey getPublicKeyByProof(Proof proof, List<PublicKey> publicKeys);
}
