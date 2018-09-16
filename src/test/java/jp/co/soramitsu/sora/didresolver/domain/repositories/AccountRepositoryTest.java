package jp.co.soramitsu.sora.didresolver.domain.repositories;

import static java.time.Instant.now;
import static java.util.Collections.singletonMap;
import static jp.co.soramitsu.sora.sdk.did.model.dto.DID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import javax.sql.DataSource;
import jp.co.soramitsu.sora.didresolver.domain.entities.Account;
import jp.co.soramitsu.sora.didresolver.domain.entities.AccountVault;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import lombok.val;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * TODO: resolve why can't properly shut down EntityManagerFactory (probably resource closing order
 * issue/not issue)
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class AccountRepositoryTest {

  @ClassRule
  public static PostgreSQLContainer pgContainer = new PostgreSQLContainer();
  @Autowired
  private AccountRepository repository;
  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void givenPersistedEntityWhenFoundByIdAssertItIsSame() {
    val account = new Account();
    account.setAccountId("Vasya");
    val vault = new AccountVault();
    val ddo = new DDO();
    ddo.setId(randomUUID());
    ddo.setCreated(now().truncatedTo(ChronoUnit.SECONDS));
    vault.setDdos(singletonMap(ddo.getId().toString(), ddo));
    account.setData(vault);
    repository.save(account);

    val persistedAccount = repository.findById(account.getAccountId());

    assertThat(persistedAccount.isPresent(), is(true));
    assertThat(persistedAccount.get(), equalTo(account));
  }

  @Test
  public void whenCalledFindDDOByDidThenReturnStringAndMapToDDO()
      throws IOException {
    val ddoString = repository
        .findDDOByDid("did:sora:soraUser8");

    assertThat(ddoString.isPresent(), is(true));

    val ddo = mapper.readValue(ddoString.get(), DDO.class);
    assertThat(ddo.getId().toString(), equalTo("did:sora:soraUser8"));
  }

  @TestConfiguration
  public static class TestConfig {

    @Bean
    public DataSource dataSource() {
      HikariConfig hikariConfig = new HikariConfig();
      hikariConfig.setUsername(pgContainer.getUsername());
      hikariConfig.setPassword(pgContainer.getPassword());
      hikariConfig.setJdbcUrl(pgContainer.getJdbcUrl());

      return new HikariDataSource(hikariConfig);
    }

    /**
     * Required for populating database from classpath:testData.json
     */
    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
      Resource sourceData = new ClassPathResource("testData.json");

      val factory = new Jackson2RepositoryPopulatorFactoryBean();
      factory.setResources(new Resource[]{sourceData});
      return factory;
    }

  }
}
