package jp.co.soramitsu.sora.didresolver.Services.Impl;

import jp.co.soramitsu.sora.didresolver.DTO.DDO;
import jp.co.soramitsu.sora.didresolver.Services.StorageService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rogachevsn
 */
@Service
@NoArgsConstructor
public class MockStorageServiceImpl implements StorageService{

    private static final Map<String,DDO> mockStorage = new ConcurrentHashMap<>();

    @Override
    public void createOrUpdate(String did, DDO ddo) {
        mockStorage.put(did, ddo);
    }

    @Override
    public DDO read(String did) {
        return mockStorage.get(did);
    }

    @Override
    public void delete(String did) {

    }
}
