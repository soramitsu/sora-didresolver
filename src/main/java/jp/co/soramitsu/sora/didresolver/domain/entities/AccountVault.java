package jp.co.soramitsu.sora.didresolver.domain.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import lombok.Data;
import lombok.val;

@Data
public class AccountVault {
  private static final ObjectMapper mapper = new ObjectMapper();

  private Map<String, DDO> ddos;

  /**
   * This constructor is required to let #{@link org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean}
   * fill this entity from string
   * */
  public AccountVault(String jsonString) throws IOException {
    ddos = new HashMap<>();
    Iterator<Entry<String, JsonNode>> iterator = mapper.readTree(jsonString).get("ddos").fields();
    while (iterator.hasNext()) {
      val entry = iterator.next();
      ddos.put(entry.getKey(), mapper.treeToValue(entry.getValue(), DDO.class));
    }
  }

  public AccountVault() {
  }
}
