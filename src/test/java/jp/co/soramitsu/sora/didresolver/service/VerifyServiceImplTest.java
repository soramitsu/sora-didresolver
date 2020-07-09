package jp.co.soramitsu.sora.didresolver.service;

import static jp.co.soramitsu.sora.sdk.json.JsonUtil.buildMapper;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import jp.co.soramitsu.sora.didresolver.exceptions.ProofSignatureVerificationException;
import jp.co.soramitsu.sora.didresolver.exceptions.PublicKeyValueNotPresentedException;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.didresolver.services.impl.VerifyServiceImpl;
import jp.co.soramitsu.sora.didresolver.util.DataProvider;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
import jp.co.soramitsu.sora.sdk.did.model.dto.publickey.Ed25519Sha3VerificationKey;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;
import jp.co.soramitsu.sora.sdk.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class VerifyServiceImplTest {

  private static final String DDO_JSON_NAME = "canonical2DDO.json";

  @SuppressWarnings("SpringJavaAutowiredMembersInspection")
  @Autowired
  private VerifyService verifyService;

  private DataProvider dataProvider = new DataProvider();

  private static JsonNode ddoJson;

  @BeforeAll
  static void setUp() throws Exception {
    ddoJson = buildMapper().readTree(VerifyServiceImplTest.class.getClassLoader().getResourceAsStream(DDO_JSON_NAME));
  }

  @Test
  void testSuccessVerifyDDOProof()
      throws IOException, ProofSignatureVerificationException, PublicKeyValueNotPresentedException {
    DDO ddo = buildMapper().readValue(ddoJson.toString(), DDO.class);
    assertTrue(verifyService.verifyIntegrityOfDDO(ddo, ddoJson));
  }

  @Test
  void testFailedVerifyDDOProofByincorrectSignature() throws IOException {
    DDO ddo = buildMapper().readValue(ddoJson.toString(), DDO.class);
    ObjectNode jsonNode = (ObjectNode) JsonUtil.deepClone(ddoJson, JsonNode.class);
    jsonNode.put("proof", "testSig");
    assertThrows(ProofSignatureVerificationException.class,
        () -> verifyService.verifyIntegrityOfDDO(ddo, jsonNode));
  }

  @Test
  void testFailedVerifyDDOProofByIncorrectLengthOfTheKey()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    DDO ddo = buildMapper().readValue(ddoJson.toString(), DDO.class);
    Ed25519Sha3VerificationKey publicKey = (Ed25519Sha3VerificationKey) ddo.getPublicKey().get(0);
    Field publicKeyField = Ed25519Sha3VerificationKey.class.getDeclaredField("publicKey");
    publicKeyField.setAccessible(true);
    publicKeyField.set(publicKey, "testKey".getBytes());
    assertThrows(IllegalArgumentException.class, () -> verifyService.verifyIntegrityOfDDO(ddo, ddoJson));
  }

  @Test
  void testSuccessIsProofCreatorInAuth() throws ParserException {
    DID proofCreator = dataProvider.getProofForTest().getOptions().getCreator();
    assertTrue(
        verifyService.isCreatorInAuth(proofCreator, dataProvider.getAuthenticationForTest()));
  }

  @Test
  void testFailedIsProofCreatorInAuth() throws ParserException {
    Proof proof = dataProvider.getProofForTest();
    List<Authentication> authentications = dataProvider.getAuthenticationForTest();
    proof
        .getOptions()
        .setCreator(
            DID.parse(
                StringUtils.replaceChars(proof.getOptions().getCreator().toString(), '8', 'l')));
    assertFalse(verifyService.isCreatorInAuth(proof.getOptions().getCreator(), authentications));
  }

  @Test
  void testSuccessIsProofInPublicKeys() throws ParserException {
    assertTrue(
        verifyService.isCreatorInPublicKeys(
            dataProvider.getProofForTest().getOptions().getCreator(),
            dataProvider.getPublicKeysForTest()));
  }

  @Test
  void testFailedIsProofInPublicKeys() throws ParserException {
    Proof proof = dataProvider.getProofForTest();
    List<PublicKey> publicKeys = dataProvider.getPublicKeysForTest();
    proof
        .getOptions()
        .setCreator(
            DID.parse(
                StringUtils.replaceChars(proof.getOptions().getCreator().toString(), '8', 'l')));
    assertFalse(verifyService.isCreatorInPublicKeys(proof.getOptions().getCreator(), publicKeys));
  }

  @TestConfiguration
  static class CryptoServiceImplTestContextConfiguration {

    @Bean
    public VerifyService cryptoService() {
      return new VerifyServiceImpl();
    }
  }
}
