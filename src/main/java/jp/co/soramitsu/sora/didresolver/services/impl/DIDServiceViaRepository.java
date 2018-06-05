package jp.co.soramitsu.sora.didresolver.services.impl;

import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.IROHA;

import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.domain.iroha.repositories.AccountRepository;
import jp.co.soramitsu.sora.didresolver.domain.iroha.valueobjects.DID;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DIDServiceViaRepository implements StorageService {

  private AccountRepository accountRepository;

  public DIDServiceViaRepository(
      AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public void createOrUpdate(String did, DDO ddo) {
    throw new UnsupportedOperationException();

  }

  @Override
  public Optional<DDO> read(String did)
      throws UnparseableException {
    val didObject = new DID(did);

    if (didObject.getType().equals(IROHA)) {
      val ddoString = accountRepository.findDDOByAccountIdAndDid(didObject.getDidString());
      return ddoString.map(s -> JacksonUtil.fromString(s, DDO.class));
    } else {
      throw new DIDNotFoundException(did);
    }
  }

  @Override
  public void delete(String did) {
    throw new UnsupportedOperationException();
  }
}
