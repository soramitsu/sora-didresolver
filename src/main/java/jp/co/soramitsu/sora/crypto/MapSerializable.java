package jp.co.soramitsu.sora.crypto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;
import java.util.TreeMap;

public interface MapSerializable {

  abstract class MapSerializableDetail {

    public static final ObjectMapper mapper = new ObjectMapper();

    static {
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      mapper.disable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
      mapper.registerModule(new JavaTimeModule());
    }

  }

  default Map<String, Object> serializeAsMap() {
    return MapSerializableDetail.mapper
        .convertValue(this, new TypeReference<Map<String, Object>>() {
        });
  }
}
