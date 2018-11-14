package jp.co.soramitsu.sora.didresolver.config;

import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.sora.didresolver.config.properties.IrohaProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IrohaBeansConfig {

  @Bean
  public IrohaAPI irohaApi(IrohaProperties irohaProperties) {
    val api = new IrohaAPI(
        irohaProperties.getConnection().getHost(),
        irohaProperties.getConnection().getPort()
    );
    log.debug("creating Iroha channel instance: {}", api.getUri());
    return api;
  }
}
