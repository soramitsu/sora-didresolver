package jp.co.soramitsu.sora.didresolver.services.impl;

import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.domain.repositories.AccountRepository;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
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
  public Optional<DDO> findDDObyDID(String did) throws UnparseableException {
    // TODO: 11/09/2018 extract validation into separete class
    DID didObject;
    try {
      didObject = DID.parse(did);
    } catch (ParserException e) {
      throw new UnparseableException(e.getMessage());
    }

    val ddoString = accountRepository.findDDOByDid(didObject.toString());
    return ddoString.map(s -> JacksonUtil.fromString(s, DDO.class));
  }

  @Override
  public void delete(String did) {
    throw new UnsupportedOperationException();
  }
}
