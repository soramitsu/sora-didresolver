package jp.co.soramitsu.sora.didresolver.services;

import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;

public interface StorageService {

  void createOrUpdate(String did, DDO ddo);

  Optional<DDO> findDDObyDID(String did) throws UnparseableException;

  /**
   * Delete DDO by DID
   *
   * @param did - valid DID
   * @throws jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException - if the DID is not
   * found in the storage
   */
  void delete(String did);
}
