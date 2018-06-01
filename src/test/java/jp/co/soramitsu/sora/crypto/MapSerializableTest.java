package jp.co.soramitsu.sora.crypto;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jp.co.soramitsu.sora.util.AllTypesPojo;
import jp.co.soramitsu.sora.util.Bencoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class MapSerializableTest {

  AllTypesPojo pojo;
  Map<String, Object> expected;
  Map<String, Object> actual;

  List<String> expectedListOfStrings = Arrays.asList("hello", "world");
  Map<String, Object> expectedMap = ImmutableMap.of("a", 1, "b", 2, "c", 3);
  Instant expectedTime = Instant.now();

  @BeforeEach
  void setUp() {
    pojo = AllTypesPojo.builder()
        .bool(true)
        .integer(42)
        .string("42?")
        .listOfStrings(expectedListOfStrings)
        .map(expectedMap)
        .time(expectedTime)
        .build();
  }

  @Test
  void serializeAsMapInOrder() throws IOException {
    expected = new TreeMap<String, Object>() {{
      put("bool", Boolean.TRUE);
      put("integer", 42);
      put("string", "42?");
      put("listOfStrings", expectedListOfStrings);
      put("map", expectedMap);
      put("time", expectedTime.toString());
    }};

    actual = pojo.serializeAsMap();

    assertEquals(
        Bencoder.immediate(expected, UTF_8),
        Bencoder.immediate(actual, UTF_8)
    );
  }
}
