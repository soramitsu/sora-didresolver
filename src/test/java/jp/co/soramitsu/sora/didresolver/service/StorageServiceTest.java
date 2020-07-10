package jp.co.soramitsu.sora.didresolver.service;

import static jp.co.soramitsu.sora.didresolver.util.DdoUtils.getDefaultDdo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.IntegrationTest;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StorageServiceTest extends IntegrationTest {

  public static final String DEFAULT_DID = "did:sora:user123";
  @Autowired
  private StorageService storageService;

  @Test
  public void canSetAndGetObject() throws Exception {
    DDO targetDdo = getDefaultDdo();
    storageService.createOrUpdate(DEFAULT_DID, targetDdo);
    final Optional<JsonNode> ddo = storageService
        .findDDObyDID(DEFAULT_DID);

    assertTrue(ddo.isPresent());
    assertEquals(mapper.valueToTree(targetDdo), ddo.get());
  }
}
