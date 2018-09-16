package jp.co.soramitsu.sora.util;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static jp.co.soramitsu.sora.sdk.did.model.dto.DID.parse;
import static jp.co.soramitsu.sora.sdk.did.model.type.SignatureTypeEnum.Ed25519Sha3Signature;
import static org.spongycastle.util.encoders.Hex.decode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.Options;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
import jp.co.soramitsu.sora.sdk.did.model.dto.authentication.Ed25519Sha3Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.publickey.Ed25519Sha3VerificationKey;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;

public class DataProvider {

  public static final String TEST_DID = "did:sora:soraUser8";

  public static final String ID_BASE = TEST_DID + "#keys-";

  public static final byte[] KEY_VALUE =
      decode("a023b2d2438baee133a9cd51507614ba80e07552ec238eb0890ccb3ee78df792");

  public static final int KEYS_COUNT = 5;

  private Random random = new Random();

  public List<PublicKey> getPublicKeysForTest() throws ParserException {
    List<PublicKey> publicKeys = new ArrayList<>();
    for (int i = 0; i < KEYS_COUNT; i++) {
      publicKeys.add(new Ed25519Sha3VerificationKey(parse(ID_BASE + i), null, KEY_VALUE));
    }
    return publicKeys;
  }

  public Proof getProofForTest() {
    try {
      Options options = new Options(
          Ed25519Sha3Signature, Instant.now(),
          parse(ID_BASE + 2),
          "23532",
          "test");
      return Proof.builder().options(options).signatureValue("test".getBytes()).build();
    } catch (ParserException e) {
    }
    return null;
  }

  public List<Authentication> getAuthenticationForTest() throws ParserException {
    List<Authentication> authentications = new ArrayList<>();
    for (int i = 0; i < KEYS_COUNT; i++) {
      authentications.add(
          new Ed25519Sha3Authentication(parse(ID_BASE + i))
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
}
