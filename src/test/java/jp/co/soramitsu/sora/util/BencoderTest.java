package jp.co.soramitsu.sora.util;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import javax.xml.bind.DatatypeConverter;
import jp.co.soramitsu.sora.util.bencoder.BencodeMapper;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class BencoderTest {

  @Test
  public void encodingIsCorrect() throws IOException {
    AllTypesPojo pojo = AllTypesPojo.builder()
        .bool(Boolean.TRUE)
        .floating(1.2d)
        .integer(42)
        .string("42?")
        .listOfStrings(Arrays.asList("hello", "world"))
        .map(new HashMap<String, Object>() {{
          put("a", 1);
          put("nullkey", null);
        }})
        .time(Instant.parse("2002-10-10T17:00:00Z"))
        .build();

    ObjectMapper mapper = new BencodeMapper();
    String actual = mapper
        .writeValueAsString(pojo);

    assertEquals(
        "d4:boolB8:floatingi1.2e7:integeri42e13:listOfStringsl5:hello5:worlde3:mapd1:ai1ee15:primitiveDoublei0.0e12:primitiveInti0e6:string3:42?4:time20:2002-10-10T17:00:00Ze",
        actual
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

    ObjectMapper mapper = new BencodeMapper();

    assertEquals(
        mapper.writeValueAsString(m1),
        mapper.writeValueAsString(m2)
    );
  }

  @Test
  public void UTF8EncodingWorks() throws JsonProcessingException {
    val m1 = new HashMap<String, Object>() {{
      put("привет мир", "こんにちは世界");
      // utf-16 will be printed as bytes since we treat all strings as utf-8 strings
      put("utf-16", new String("фить-ха".getBytes(UTF_8), UTF_16));
      put("hello", 1337);
    }};

    ObjectMapper mapper = new BencodeMapper();

    byte[] bytes = mapper.writeValueAsBytes(m1);
    String string = mapper.writeValueAsString(m1);

    assertEquals(
        93,
        bytes.length
    );
    assertEquals(
        "64353A68656C6C6F693133333765363A7574662D313632313AED8684ED82B8ED8682ED868CE2B791E89790EFBFBD31393AD0BFD180D0B8D0B2D0B5D18220D0BCD0B8D18032313AE38193E38293E381ABE381A1E381AFE4B896E7958C65",
        DatatypeConverter.printHexBinary(bytes)
    );
    assertEquals(
        "d5:helloi1337e6:utf-1621:톄킸톂톌ⷑ藐�19:привет мир21:こんにちは世界e",
        string
    );
  }
}
