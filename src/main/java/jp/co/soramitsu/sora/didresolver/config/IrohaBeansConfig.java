package jp.co.soramitsu.sora.didresolver.config;

import static io.grpc.ManagedChannelBuilder.forAddress;

import io.grpc.ManagedChannel;
import jp.co.soramitsu.sora.didresolver.config.properties.IrohaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IrohaBeansConfig {

  @Bean
  public ManagedChannel irohaChannel(IrohaProperties irohaProperties) {
    return forAddress(
        irohaProperties.getConnection().getHost(), irohaProperties.getConnection().getPort())
        .usePlaintext()
        .build();
  }
}
