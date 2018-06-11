package jp.co.soramitsu.sora.didresolver.domain.repositories;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import javax.sql.DataSource;
import jp.co.soramitsu.sora.didresolver.domain.entities.Account;
import jp.co.soramitsu.sora.didresolver.domain.entities.AccountVault;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
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
 * TODO: resolve why can't properly shut down EntityManagerFactory (probably resource closing
 * order issue/not issue)
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class AccountRepositoryTest {

  @Autowired
  private AccountRepository repository;

  @ClassRule
  public static PostgreSQLContainer pgContainer = new PostgreSQLContainer();

  private ObjectMapper mapper = new ObjectMapper();

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
  public void givenPersistedEntityWhenFoundByIdAssertItIsSame() {
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
  public void whenCalledFindDDOByDidThenReturnStringAndMapToDDO()
      throws IOException {
    val ddoString = repository
        .findDDOByDid("did:sora:iroha:bogdan@soramitsu.co.jp");

    assertThat(ddoString.isPresent(), is(true));

    val ddo = mapper.readValue(ddoString.get(), DDO.class);
    assertThat(ddo.getId(), equalTo("did:sora:iroha:bogdan@soramitsu.co.jp"));
  }
}
