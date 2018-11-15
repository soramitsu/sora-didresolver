package jp.co.soramitsu.sora.didresolver;

import static java.lang.System.setProperty;
import static java.time.Instant.now;
import static jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.publicKeyFromBytes;
import static jp.co.soramitsu.iroha.testcontainers.PeerConfig.builder;
import static jp.co.soramitsu.iroha.testcontainers.detail.GenesisBlockBuilder.defaultKeyPair;
import static org.bouncycastle.util.encoders.Hex.decode;

import jp.co.soramitsu.iroha.java.Transaction;
import jp.co.soramitsu.iroha.testcontainers.IrohaContainer;
import jp.co.soramitsu.iroha.testcontainers.detail.GenesisBlockBuilder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "logging.level.jp.co.soramitsu.sora=debug"
    },
    classes = {DIDResolverApplication.class})
@Configuration
public abstract class IntegrationTest {

  public static final String PUBLICKEY =
      "313a07e6384776ed95447710d15e59148473ccfc052a681317a72a69f2a49910";
  public static final String PRIVATEKEY =
      "f101537e319568c765b2cc89698325604991dca57b9716b58016b253506cab70";

  private static IrohaContainer iroha = new IrohaContainer().withPeerConfig(
      builder()
          .genesisBlock(
              new GenesisBlockBuilder()
                  .addDefaultTransaction()
                  .addTransaction(
                      Transaction.builder(null, now())
                          .createAccount("admin", "test", publicKeyFromBytes(decode(PUBLICKEY)))
                          .sign(defaultKeyPair)
                          .build()
                  ).build())
          .build());

  static {
    iroha.start();
    setProperty("DIDRESOLVER_IROHA_PUBLIC_KEY", PUBLICKEY);
    setProperty("DIDRESOLVER_IROHA_PRIVATE_KEY", PRIVATEKEY);
    setProperty("DIDRESOLVER_IROHA_HOST", iroha.getToriiAddress().getHost());
    setProperty("DIDRESOLVER_IROHA_PORT", String.valueOf(iroha.getToriiAddress().getPort()));
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  protected TestRestTemplate testRestTemplate;
}
