package jp.co.soramitsu.sora.didresolver.services;

import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;

public interface StorageService {

  void createOrUpdate(String did, DDO ddo);

  Optional<DDO> read(String did) throws UnparseableException;

  /**
   * Delete DDO by DID
   *
   * @param did - valid DID
   * @throws jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException - if the DID is not
   * found in the storage
   */
  void delete(String did);
}
