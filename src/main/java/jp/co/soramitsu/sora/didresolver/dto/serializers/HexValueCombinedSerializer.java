package jp.co.soramitsu.sora.didresolver.dto.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;

/**
 * Class for custom process of serialize/deserialize string in HEX encode to bytes
 */
public class HexValueCombinedSerializer {

  public static class HexValueSerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeString(DatatypeConverter.printHexBinary(value));
    }
  }

  public static class HexValueDeserializer extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      return DatatypeConverter.parseHexBinary(p.getText());
    }
  }
}
