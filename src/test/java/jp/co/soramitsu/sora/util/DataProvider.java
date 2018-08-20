package jp.co.soramitsu.sora.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;
import jp.co.soramitsu.sora.didresolver.commons.CryptoActionTypeEnum;
import jp.co.soramitsu.sora.didresolver.commons.CryptoTypeEnum;
import jp.co.soramitsu.sora.didresolver.dto.Authentication;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;

public class DataProvider {

  public static final String DID = "did:sora:uuid:caab4570-5f3f-4050-8d61-15306dea4bcf";

  public static final String ID_BASE = DID + "#keys-";

  public static final byte[] KEY_VALUE = DatatypeConverter
      .parseHexBinary(
          "A023B2D2438BAEE133A9CD51507614BA80E07552EC238EB0890CCB3EE78DF792");

  public static final int KEYS_COUNT = 5;

  private Random random = new Random();

  public List<PublicKey> getPublicKeysForTest() {
    List<PublicKey> publicKeys = new ArrayList<>();
    for (int i = 0; i < KEYS_COUNT; i++) {
      publicKeys.add(
          new PublicKey(URI.create(ID_BASE + i), getCryptoType(CryptoActionTypeEnum.VERIFY), null,
              KEY_VALUE));
    }
    return publicKeys;
  }

  public Proof getProofForTest() {
    return new Proof(getCryptoType(CryptoActionTypeEnum.SIGNATURE),
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        URI.create(ID_BASE + random.nextInt(KEYS_COUNT)), KEY_VALUE, null, null);
  }

  public List<Authentication> getAuthenticationForTest() {
    List<Authentication> authentications = new ArrayList<>();
    for (int i = 0; i < KEYS_COUNT; i++) {
      authentications.add(
          new Authentication(getCryptoType(CryptoActionTypeEnum.AUTH), URI.create(ID_BASE + i),
              null,
              KEY_VALUE));
    }
    return authentications;
  }

  public DDO getDDOFromJson(String fileName) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(Include.NON_NULL);

    return objectMapper
        .readValue(
            DataProvider.class
                .getClassLoader()
                .getResourceAsStream(fileName),
            DDO.class
        );
  }

  private String getCryptoType(CryptoActionTypeEnum actionTypeEnum) {
    return CryptoTypeEnum.getCryptoTypes(actionTypeEnum).get(0);
  }
}
