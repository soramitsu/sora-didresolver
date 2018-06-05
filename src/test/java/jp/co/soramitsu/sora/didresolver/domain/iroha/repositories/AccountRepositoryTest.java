package jp.co.soramitsu.sora.didresolver.domain.iroha.repositories;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import javax.sql.DataSource;
import jp.co.soramitsu.sora.didresolver.domain.iroha.entities.Account;
import jp.co.soramitsu.sora.didresolver.domain.iroha.entities.AccountVault;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TODO: resolve why can't properly shut down EntityManagerFactory (probably resource closing
 * order issue/not issue)
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Slf4j
public class AccountRepositoryTest {

  @Autowired
  AccountRepository repository;

  @Autowired
  TestEntityManager entityManager;

  private ObjectMapper mapper = new ObjectMapper();

  @TestConfiguration
  public static class TestConfig {

    @Bean(destroyMethod = "close")
    public EmbeddedPostgres embeddedPostgres() throws IOException {
      return EmbeddedPostgres.start();
    }

    @Bean
    public DataSource irohaDS(EmbeddedPostgres embeddedPostgres) {
      return embeddedPostgres.getPostgresDatabase();
    }

    /**
     * Required for populating database from classpath:testData.json
     * */
    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
      Resource sourceData = new ClassPathResource("testData.json");

      val factory = new Jackson2RepositoryPopulatorFactoryBean();
      factory.setResources(new Resource[]{sourceData});
      return factory;
    }

  }

  @Test
  public void given_persisted_entity__when_found_by_id_assert_it_is_same() {
    val account = new Account();
    account.setAccountId("Vasya");
    val vault = new AccountVault();
    val ddo = new DDO();
    ddo.setId("someId");
    ddo.setCreated(Timestamp.from(Instant.now()));
    vault.setDdos(Collections.singletonMap("someId", ddo));
    account.setData(vault);
    repository.save(account);

    val persistedAccount = repository.findById(account.getAccountId());

    assertThat(persistedAccount.isPresent(), is(true));
    assertThat(persistedAccount.get(), equalTo(account));
  }

  @Test
  public void given_repository_when_called_findByAccountIdAndDid_assert_string_returned_and_mapped_to_DDO()
      throws IOException {
    val ddoString = repository
        .findDDOByAccountIdAndDid("did:sora:iroha:bogdan@soramitsu.co.jp");

    assertThat(ddoString.isPresent(), is(true));

    val ddo = mapper.readValue(ddoString.get(), DDO.class);
    assertThat(ddo.getId(), equalTo("did:sora:iroha:bogdan@soramitsu.co.jp"));
  }
}
