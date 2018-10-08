package jp.co.soramitsu.sora.didresolver.config.properties;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.privateKeyFromBytes;
import static jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.publicKeyFromBytes;

import java.security.KeyPair;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "iroha", ignoreUnknownFields = false)
@Component
@Data
public class IrohaProperties {

  @NestedConfigurationProperty
  private AccountProperties account = new AccountProperties();

  private String sharedAccountName;

  @NestedConfigurationProperty
  private ConnectionProperties connection = new ConnectionProperties();

  @Data
  public static class AccountProperties {

    private String name;
    private String privateKey;
    private String publicKey;

    public KeyPair keyPair() {
      return new KeyPair(
          publicKeyFromBytes(parseHexBinary(publicKey)),
          privateKeyFromBytes(parseHexBinary(privateKey)));
    }
  }

  @Data
  public static class ConnectionProperties {

    private String host;
    private int port;
  }
}
