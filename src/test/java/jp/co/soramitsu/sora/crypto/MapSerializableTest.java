package jp.co.soramitsu.sora.crypto;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jp.co.soramitsu.sora.util.AllTypesPojo;
import jp.co.soramitsu.sora.util.Bencoder;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MapSerializableTest {

  private AllTypesPojo pojo;

  private List<String> expectedListOfStrings = Arrays.asList("hello", "world");
  private Map<String, Object> expectedMap = ImmutableMap.of("a", 1, "b", 2, "c", 3);
  private Instant expectedTime = Instant.now();

  @Before
  public void setUp() {
    pojo = AllTypesPojo.builder()
        .bool(Boolean.TRUE)
        .integer(42)
        .string("42?")
        .listOfStrings(expectedListOfStrings)
        .map(expectedMap)
        .time(expectedTime)
        .build();
  }

  @Test
  public void serializeAsMapInOrder() throws IOException {
    val expected = new TreeMap<String, Object>() {{
      put("bool", Boolean.TRUE);
      put("integer", 42);
      put("string", "42?");
      put("listOfStrings", expectedListOfStrings);
      put("map", expectedMap);
      put("time", expectedTime.toString());
    }};

    val actual = pojo.serializeAsMap();

    assertEquals(
        Bencoder.immediate(expected, UTF_8),
        Bencoder.immediate(actual, UTF_8)
    );
  }
}
