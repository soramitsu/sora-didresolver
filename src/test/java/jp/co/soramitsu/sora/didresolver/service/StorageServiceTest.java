package jp.co.soramitsu.sora.didresolver.service;

import static jp.co.soramitsu.sora.didresolver.util.DdoUtils.getDefaultDdo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.IrohaIntegrationTest;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class StorageServiceTest extends IrohaIntegrationTest {

  public static final String DEFAULT_DID = "did:sora:user123";
  @Autowired
  private StorageService storageService;

  @Test
  public void canSetAndGetObject() throws Exception {
    DDO targetDdo = getDefaultDdo();
    storageService.createOrUpdate(DEFAULT_DID, targetDdo);
    final Optional<DDO> ddo = storageService
        .findDDObyDID(DEFAULT_DID);

    assertTrue(ddo.isPresent());
    assertEquals(targetDdo, ddo.get());
  }
}
