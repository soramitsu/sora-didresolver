package jp.co.soramitsu.sora.didresolver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.soramitsu.sora.sdk.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MiscBeansConfig {

  @Bean
  public ObjectMapper mapper() {
    return JsonUtil.buildMapper();
  }
}
