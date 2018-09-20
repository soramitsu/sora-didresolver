package jp.co.soramitsu.sora.didresolver;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.jayway.jsonpath.JsonPath.read;
import static java.util.Collections.singletonList;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.UUID;
import javax.sql.DataSource;
import jp.co.soramitsu.sora.didresolver.IntegrationTest.TestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "logging.level.jp.co.soramitsu.sora=debug"
    },
    classes = {DIDResolverApplication.class, TestConfig.class})
@Configuration
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class IntegrationTest implements InitializingBean {

  public static final String PUBLICKEY =
      "313a07e6384776ed95447710d15e59148473ccfc052a681317a72a69f2a49910";
  public static final String PRIVATEKEY =
      "f101537e319568c765b2cc89698325604991dca57b9716b58016b253506cab70";
  public static final String SHARED_ACCOUNT_PRIVATEKEY = "7e00405ece477bb6dd9b03a78eee4e708afc2f5bcdce399573a5958942f4a390";
  public static final String SHARED_ACCOUNT_PUBLICKEY = "716fe505f69f18511a1b083915aa9ff73ef36e6688199f3959750db38b8f4bfc";
  public static final String OWN_DID = "did:sora:uuid:fakeuuid";
  public static final String BCA_NAME = "BCA_FINANCE";
  public static final String RULES_SERVICE_DID = "did:sora:uuid:rulesdid";
  public static final String TRANSFER_DATA_DID = "did:sora:uuid:transferdatadid";
  public static final String OTHER_DIDS = "did:sora:uuid:unit2";

  static {
    System.setProperty("IROHA_PUBLIC_KEY", PUBLICKEY);
    System.setProperty("IROHA_PRIVATE_KEY", PRIVATEKEY);
    System.setProperty("OWN_DID", OWN_DID);
    System.setProperty("BCA_NAME", BCA_NAME);
    System.setProperty("RULES_SERVICE_DID", RULES_SERVICE_DID);
    System.setProperty("OTHER_DIDS", OTHER_DIDS);
  }

  private static PostgreSQLContainer postgres = new PostgreSQLContainer();

  protected static final String $_ERROR = "$.error";
  protected static final String TOKEN = "$.token";
  protected static final String $_MESSAGE = "$.message";

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  protected TestRestTemplate testRestTemplate;
  protected UUID defaultUserUid = UUID.randomUUID();

  @Override
  public void afterPropertiesSet() {

  }

  @BeforeAll
  public static void setUpTest() {
    postgres.start();
  }

  @AfterAll
  public static void tearDownTest() {
    postgres.stop();
  }

  @TestConfiguration
  @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
  @AutoConfigureBefore({SecurityAutoConfiguration.class})
  public static class TestConfig {

    @Bean
    public DataSource dataSource() {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(postgres.getJdbcUrl());
      config.setUsername(postgres.getUsername());
      config.setPassword(postgres.getPassword());

      return new HikariDataSource(config);
    }
  }

  protected <T> HttpEntity<T> createHttpEntity(ResponseEntity<String> response, T body) {
    String token = read(response.getBody(), TOKEN);
    return createHttpEntity(token, body);
  }

  protected <T> HttpEntity<T> createHttpEntity(String token, T body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(singletonList(MediaType.APPLICATION_JSON_UTF8));
    if (token != null) {
      headers.set(AUTHORIZATION, "Bearer " + token);
    }
    return new HttpEntity<>(body, headers);
  }
}
