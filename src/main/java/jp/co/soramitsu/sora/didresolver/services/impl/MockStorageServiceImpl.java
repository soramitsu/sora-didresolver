package jp.co.soramitsu.sora.didresolver.services.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
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
  public Optional<DDO> findDDObyDID(String did) {
    return Optional.ofNullable(mockStorage.get(did));
  }

  @Override
  public void delete(String did) {
    if (!mockStorage.containsKey(did)) {
      throw new DIDNotFoundException(did);
    }
    mockStorage.remove(did);
  }
}
