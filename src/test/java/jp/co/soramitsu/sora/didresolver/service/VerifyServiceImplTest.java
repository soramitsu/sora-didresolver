package jp.co.soramitsu.sora.didresolver.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SignatureException;
import java.util.List;
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
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class VerifyServiceImplTest {

  private static final String DDO_JSON_NAME = "canonical2DDO.json";

  @Autowired
  private VerifyService verifyService;

  private DataProvider dataProvider = new DataProvider();

  @Test
  public void testSuccessVerifyDDOProof() throws IOException {
    DDO ddo = dataProvider.getDDOFromJson(DDO_JSON_NAME);
    assertTrue(verifyService.verifyIntegrityOfDDO(ddo));
  }

  @Test
  public void testFailedVerifyDDOProofByincorrectSignature()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    DDO ddo = dataProvider.getDDOFromJson(DDO_JSON_NAME);
    Field proof = ddo.getProof().getClass().getDeclaredField("signatureValue");
    proof.setAccessible(true);
    proof.set(ddo.getProof(), "testSig".getBytes());
    assertThrows(SignatureException.class, () -> verifyService.verifyIntegrityOfDDO(ddo));
  }

  @Test
  public void testFailedVerifyDDOProofByIncorrectLengthOfTheKey()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    DDO ddo = dataProvider.getDDOFromJson(DDO_JSON_NAME);
    Ed25519Sha3VerificationKey publicKey = (Ed25519Sha3VerificationKey) ddo.getPublicKey().get(0);
    Field publicKeyField = Ed25519Sha3VerificationKey.class.getDeclaredField("publicKey");
    publicKeyField.setAccessible(true);
    publicKeyField.set(publicKey, "testKey".getBytes());
    assertThrows(IllegalArgumentException.class, () -> verifyService.verifyIntegrityOfDDO(ddo));
  }

  @Test
  public void testSuccessIsProofCreatorInAuth() throws ParserException {
    DID proofCreator = dataProvider.getProofForTest().getOptions().getCreator();
    assertTrue(
        verifyService.isCreatorInAuth(proofCreator, dataProvider.getAuthenticationForTest()));
  }

  @Test
  public void testFailedIsProofCreatorInAuth() throws ParserException {
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
  public void testSuccessIsProofInPublicKeys() throws ParserException {
    assertTrue(
        verifyService.isCreatorInPublicKeys(
            dataProvider.getProofForTest().getOptions().getCreator(),
            dataProvider.getPublicKeysForTest()));
  }

  @Test
  public void testFailedIsProofInPublicKeys() throws ParserException {
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
