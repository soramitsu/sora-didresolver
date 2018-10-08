package jp.co.soramitsu.sora.didresolver.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.services.IrohaService;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

  private final IrohaService irohaService;
  private final ObjectMapper mapper;

  @Autowired
  public StorageServiceImpl(ObjectMapper mapper, IrohaService irohaService) {
    this.irohaService = irohaService;
    this.mapper = mapper;
  }

  @Override
  public void createOrUpdate(String did, DDO ddo) {
    irohaService.setAccountDetails(did, ddo);
  }

  @Override
  public Optional<DDO> findDDObyDID(String did) {
    final Optional<String> accountDetails = irohaService
        .getAccountDetails(did);
    return accountDetails.flatMap(this::parseDdoFromIrohaResponse);
  }

  @Override
  public void delete(String did) {
    //  TODO: deleting DDO from Iroha
  }

  private Optional<DDO> parseDdoFromIrohaResponse(String response) {
    try {
      DDO result = mapper.readValue(response, DDO.class);
      return Optional.of(result);
    } catch (IOException e) {
      throw new RuntimeException("Exception while parsing ddo");
    }
  }

}
