package jp.co.soramitsu.sora.didresolver.services.impl;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.exceptions.DDOUnparseableException;
import jp.co.soramitsu.sora.didresolver.services.IrohaService;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
  public Optional<DDO> findDDObyDID(String did) throws DDOUnparseableException {
    val accountDetails = irohaService.getAccountDetails(did);
    if (accountDetails.isPresent()) {
      return parseDdoFromIrohaResponse(accountDetails.get());
    }
    return empty();
  }

  @Override
  public void delete(String did) {
    irohaService.setAccountDetails(did, null);
  }

  private Optional<DDO> parseDdoFromIrohaResponse(String response) throws DDOUnparseableException {
    try {
      DDO result = mapper.readValue(response, DDO.class);
      return ofNullable(result);
    } catch (IOException e) {
      throw new DDOUnparseableException(e);
    }
  }

}
