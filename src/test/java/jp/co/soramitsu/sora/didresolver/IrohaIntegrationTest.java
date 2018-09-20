package jp.co.soramitsu.sora.didresolver;

import static org.testcontainers.containers.BindMode.READ_ONLY;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;

@Slf4j
public class IrohaIntegrationTest extends IntegrationTest {

  private static String irohaVersion = "1.0.0_beta-4";
  protected static final String PG_USER = "postgres";
  protected static final String PG_PASSWORD = "mysecretpassword";
  protected static final String POSTGRES_USER = "POSTGRES_USER";
  protected static final String POSTGRES_PASSWORD = "POSTGRES_PASSWORD";
  protected static final String POSTGRES_HOST = "POSTGRES_HOST";
  protected static final String POSTGRES_PORT = "POSTGRES_PORT";
  protected static final String KEY = "KEY";
  protected static final String NODE_KEYPAIR = "node0";
  protected static final String NETWORK_ID = "private";
  protected static Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
  protected static Network network = Network.builder().id(NETWORK_ID).build();
  protected static PostgreSQLContainer irohaPostgres =
      (PostgreSQLContainer)
          new PostgreSQLContainer()
              .withUsername(PG_USER)
              .withPassword(PG_PASSWORD)
              .withNetwork(network)
              .withNetworkAliases("some-postgres");
  protected static FixedHostPortGenericContainer iroha =
      (FixedHostPortGenericContainer)
          new FixedHostPortGenericContainer("hyperledger/iroha:" + irohaVersion)
              .withFixedExposedPort(50051, 50051)
              .withClasspathResourceMapping(
                  "iroha-compose/irohaconfigs/example", "/opt/iroha_data", READ_ONLY)
              .withEnv(KEY, NODE_KEYPAIR)
              .withEnv(POSTGRES_HOST, irohaPostgres.getContainerIpAddress())
              .withEnv(POSTGRES_USER, irohaPostgres.getUsername())
              .withEnv(POSTGRES_PASSWORD, irohaPostgres.getPassword())
              .withNetwork(network)
              .withExposedPorts(50051)
              .withLogConsumer(logConsumer)
              .waitingFor(new HostPortWaitStrategy().withStartupTimeout(Duration.ofSeconds(2)));

  @BeforeAll
  public static void setUpIrohaTest() {
    irohaPostgres.start();
    iroha.withEnv(POSTGRES_PORT, irohaPostgres.getMappedPort(5432).toString()).start();
  }

  @AfterAll
  public static void tearDownIrohaTest() {
    iroha.stop();
    irohaPostgres.stop();
  }
}
