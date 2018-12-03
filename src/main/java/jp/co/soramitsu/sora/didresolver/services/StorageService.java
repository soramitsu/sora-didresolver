package jp.co.soramitsu.sora.didresolver.services;

import java.util.Optional;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;

public interface StorageService {

  void createOrUpdate(String did, DDO ddo);

  Optional<DDO> findDDObyDID(String did);

  /**
   * Delete DDO by DID
   *
   * @param did - valid DID
   */
  void delete(String did);
}
