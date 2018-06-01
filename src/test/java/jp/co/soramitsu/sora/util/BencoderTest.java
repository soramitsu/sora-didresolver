package jp.co.soramitsu.sora.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import lombok.val;
import org.junit.jupiter.api.Test;

class BencoderTest {

  @Test
  public void encodingIsCorrect() throws IOException {
    AllTypesPojo pojo = AllTypesPojo.builder()
        .bool(true)
        .integer(42)
        .string("42?")
        .listOfStrings(Arrays.asList("hello", "world"))
        .map(ImmutableMap.of("a", 1, "b", 2, "c", 3))
        .time(Instant.parse("2002-10-10T17:00:00Z"))
        .build();

    val bencoded = Bencoder.immediate(pojo.serializeAsMap(), UTF_8);

    assertEquals(
        "d4:boolB7:integeri42e13:listOfStringsl5:hello5:worlde3:mapd1:ai1e1:bi2e1:ci3ee6:string3:42?4:time20:2002-10-10T17:00:00Ze",
        bencoded
    );
  }

  /**
   * Encoding is stable if and only if same maps (independently on entry order) give exactly same
   * bencoding.
   */
  @Test
  public void encodingIsStable() throws IOException {
    val m1 = new TreeMap<String, Object>() {{
      put("a", 1);
      put("Ω", 2);
      put("c", 3);
      put("1", 4);
      put("2", 5);
    }};

    val m2 = new HashMap<String, Object>() {{
      put("Ω", 2);
      put("a", 1);
      put("c", 3);
      put("2", 5);
      put("1", 4);
    }};

    val b1 = Bencoder.immediate(m1, UTF_8);
    val b2 = Bencoder.immediate(m2, UTF_8);

    assertEquals(b1, b2);
  }
}
