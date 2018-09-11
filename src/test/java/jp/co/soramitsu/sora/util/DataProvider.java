package jp.co.soramitsu.sora.util;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
import jp.co.soramitsu.sora.sdk.did.model.dto.authentication.Ed25519Sha3Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.publickey.Ed25519Sha3VerificationKey;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;

public class DataProvider {

  public static final String TEST_DID = "did:sora:uuid:caab4570-5f3f-4050-8d61-15306dea4bcf";

  public static final String ID_BASE = TEST_DID + "#keys-";

  public static final byte[] KEY_VALUE =
      DatatypeConverter.parseHexBinary(
          "A023B2D2438BAEE133A9CD51507614BA80E07552EC238EB0890CCB3EE78DF792");

  public static final int KEYS_COUNT = 5;

  private Random random = new Random();

  public List<PublicKey> getPublicKeysForTest() {
    List<PublicKey> publicKeys = new ArrayList<>();
    for (int i = 0; i < KEYS_COUNT; i++) {
      publicKeys.add(new Ed25519Sha3VerificationKey(DID.randomUUID(), null, KEY_VALUE));
    }
    return publicKeys;
  }

  public Proof getProofForTest() {
    return new Proof();
  }

  public List<Authentication> getAuthenticationForTest() throws ParserException {
    List<Authentication> authentications = new ArrayList<>();
    for (int i = 0; i < KEYS_COUNT; i++) {
      authentications.add(
          new Ed25519Sha3Authentication(DID.parse(TEST_DID))
      );
    }
    return authentications;
  }

  public DDO getDDOFromJson(String fileName) throws IOException {
    ObjectMapper objectMapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(INDENT_OUTPUT)
            .setSerializationInclusion(NON_NULL);

    return objectMapper.readValue(
        DataProvider.class.getClassLoader().getResourceAsStream(fileName), DDO.class);
  }

  private String getCryptoType(CryptoActionTypeEnum actionTypeEnum) {
    return CryptoTypeEnum.getCryptoTypes(actionTypeEnum).get(0);
  }
}
