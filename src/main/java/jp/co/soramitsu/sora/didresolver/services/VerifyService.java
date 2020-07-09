package jp.co.soramitsu.sora.didresolver.services;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import jp.co.soramitsu.sora.didresolver.exceptions.ProofSignatureVerificationException;
import jp.co.soramitsu.sora.didresolver.exceptions.PublicKeyValueNotPresentedException;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;

public interface VerifyService {

  /**
   * Check that DID in the list of Public keys
   *
   * @param creator value of field creator of proof element
   * @param publicKeys array of public keys from DDO
   * @return true if public keys contains key with id equals value of creator, otherwise false
   */
  boolean isCreatorInPublicKeys(DID creator, List<PublicKey> publicKeys);

  /**
   * Check that DID in the list of Authentications
   *
   * @param creator value of field creator of proof section
   * @param authentication array of values of authentication section.
   */
  boolean isCreatorInAuth(DID creator, List<Authentication> authentication);

  /**
   * Verifies integrity of the DDO
   *
   * @param ddo - DDO that is needed to be verified
   * @param jsonDDO - JSON representation of the ddo. Not necessary fully match to {@code ddo}
   */
  boolean verifyIntegrityOfDDO(DDO ddo, JsonNode jsonDDO)
      throws ProofSignatureVerificationException, PublicKeyValueNotPresentedException;
}
