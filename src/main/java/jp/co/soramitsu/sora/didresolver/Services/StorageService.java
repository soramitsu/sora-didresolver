package jp.co.soramitsu.sora.didresolver.services;

import jp.co.soramitsu.sora.didresolver.dto.DDO;

/**
 * @author rogachevsn
 */
public interface StorageService {

    void createOrUpdate(String did, DDO ddo);

    DDO read(String did);

    void delete(String did);
}
