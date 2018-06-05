package jp.co.soramitsu.sora.didresolver.domain.iroha.repositories;

import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.domain.iroha.entities.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {

  @Query(value = "select a.data#>>array['ddos', :did] d from account a where a.data is not null and a.data#>array['ddos', :did] is not null", nativeQuery = true)
  Optional<String> findDDOByAccountIdAndDid(@Param("did") String did);

}
