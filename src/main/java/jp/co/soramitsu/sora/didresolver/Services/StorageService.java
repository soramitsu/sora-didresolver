package jp.co.soramitsu.sora.didresolver.Services;

import jp.co.soramitsu.sora.didresolver.DTO.DDO;

/**
 * @author rogachevsn
 */
public interface StorageService {

    void createOrUpdate(String did, DDO ddo);

    DDO read(String did);

    void delete(String did);
}
