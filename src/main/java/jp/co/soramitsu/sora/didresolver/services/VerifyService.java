package jp.co.soramitsu.sora.didresolver.services;

import java.util.List;
import java.util.Optional;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;

public interface  VerifyService {

  /**
   * Check that DID in the list of Public keys
   *
   * @param creator value of field creator of proof element
   * @param publicKeys array of public keys from DDO
   * @return true if public keys contains key with id equals value of creator, otherwise false
   */
  boolean isProofInPublicKeys(DID creator, List<PublicKey> publicKeys);

  /**
   * Check that DID in the list of Authentications
   *
   * @param creator value of field creator of proof section
   * @param authentication array of values of authentication section.
   */
  boolean isProofCreatorInAuth(DID creator, List<Authentication> authentication);

  /**
   * Verifies integrity of the DDO
   *
   * @param ddo - DDO
   * @param publicKeyValue - public key for verifying the DDO
   * @return true if DDO proof valid, otherwise return false
   */
  boolean verifyIntegrityOfDDO(DDO ddo, byte[] publicKeyValue);
}
