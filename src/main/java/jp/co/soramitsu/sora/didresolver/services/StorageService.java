package jp.co.soramitsu.sora.didresolver.services;

import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;

public interface StorageService {

  void createOrUpdate(String did, DDO ddo) throws UnparseableException;

  Optional<DDO> read(String did) throws UnparseableException;

  void delete(String did) throws UnparseableException;
}
