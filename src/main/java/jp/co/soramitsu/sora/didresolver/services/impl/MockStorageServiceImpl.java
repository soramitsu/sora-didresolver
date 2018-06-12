package jp.co.soramitsu.sora.didresolver.services.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mocked")
@NoArgsConstructor
public class MockStorageServiceImpl implements StorageService {

  private static final Map<String, DDO> mockStorage = new ConcurrentHashMap<>();

  @Override
  public void createOrUpdate(String did, DDO ddo) {
    mockStorage.put(did, ddo);
  }

    @Override
    public Optional<DDO> read(String did) {
        return Optional.ofNullable(mockStorage.get(did));
    }

  @Override
  public void delete(String did) {
    mockStorage.remove(did);
  }
}
