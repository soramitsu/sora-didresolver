package jp.co.soramitsu.sora.didresolver.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.exceptions.DDOUnparseableException;
import jp.co.soramitsu.sora.didresolver.services.IrohaService;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.sdk.json.JsonUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StorageServiceImpl implements StorageService {

  private final IrohaService irohaService;
  private ObjectMapper mapper = JsonUtil.buildMapper();

  @Override
  public void createOrUpdate(String did, Object ddo) {
    irohaService.setAccountDetails(did, ddo);
  }

  @Override
  public Optional<JsonNode> findDDObyDID(String did) throws DDOUnparseableException {
    return irohaService.getAccountDetails(did)
        .map(this::parseDdoFromIrohaResponse)
        .filter(jsonNode -> !jsonNode.isNull());
  }

  @Override
  public void delete(String did) {
    irohaService.setAccountDetails(did, null);
  }

  @SneakyThrows(DDOUnparseableException.class)
  private JsonNode parseDdoFromIrohaResponse(String response) {
    try {
      return mapper.readTree(response);
    } catch (IOException e) {
      throw new DDOUnparseableException(e);
    }
  }

}
