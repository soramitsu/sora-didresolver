package jp.co.soramitsu.sora.didresolver.services.impl;

import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        mockStorage.remove(did);
    }
}
