package jp.co.soramitsu.sora.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;
import jp.co.soramitsu.sora.crypto.Consts;
import jp.co.soramitsu.sora.didresolver.commons.CryptoTypeEnum;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;
import jp.co.soramitsu.sora.didresolver.services.CryptoService;
import jp.co.soramitsu.sora.didresolver.services.impl.CryptoServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CryptoServiceImplTest {

  private static final String DID = "did:sora:uuid:caab4570-5f3f-4050-8d61-15306dea4bcf";

  private static final String ID_BASE = DID + "#keys-";

  private static final byte[] KEY_VALUE = DatatypeConverter
      .parseHexBinary("02b97c30de767f084ce3080168ee293053ba33b235d7116a3263d29f1450936b71");

  private static final String CRYPTO_TYPE = CryptoTypeEnum.Ed25519Sha3Signature.toString();

  private static final int KEYS_COUNT = 5;

  private Random random = new Random();

  @TestConfiguration
  static class CryptoServiceImplTestContextConfiguration {

    @Bean
    public CryptoService employeeService() {
      return new CryptoServiceImpl();
    }
  }

  @Autowired
  private CryptoService cryptoService;

  @Test
  public void testSuccessGetPublicKeyByProof() {
    PublicKey publicKey = cryptoService
        .getPublicKeyByProof(getProofForTest(), getPublicKeysForTest());
    assertNotNull(publicKey);
  }

  @Test
  public void testFailedGetPublicKeyByProof() {
    Proof proof = getProofForTest();
    proof.setCreator(ID_BASE + KEYS_COUNT + 2);
    List<PublicKey> publicKeys = getPublicKeysForTest();
    PublicKey publicKey = cryptoService
        .getPublicKeyByProof(proof, publicKeys);
    assertNull(publicKey);
  }

  @Test
  public void testSuccessCheckProofCorrectness() {
    assertTrue(cryptoService.checkProofCorrectness(getProofForTest(), DID, getPublicKeysForTest()));
  }

  @Test
  public void testWrongDIDFormatInCheckProofCorrectness() {
    Proof proof = getProofForTest();
    proof
        .setCreator(StringUtils.replaceChars(proof.getCreator(), Consts.DID_URI_DETERMINATOR, '@'));
    assertFalse(cryptoService.checkProofCorrectness(proof, DID, getPublicKeysForTest()));
  }

  @Test
  public void testFailedCheckProofCorrectness() {
    Proof proof = getProofForTest();
    List<PublicKey> publicKeys = getPublicKeysForTest();
    String creator = proof.getCreator();
    proof.setCreator(StringUtils.replaceChars(proof.getCreator(), '5', '8'));
    assertFalse(cryptoService.checkProofCorrectness(proof, DID, publicKeys));
    proof.setCreator(ID_BASE + KEYS_COUNT + 2);
    assertFalse(cryptoService.checkProofCorrectness(proof, DID, publicKeys));
  }

  private List<PublicKey> getPublicKeysForTest() {
    List<PublicKey> publicKeys = new ArrayList<>();
    for (int i = 0; i < KEYS_COUNT; i++) {
      publicKeys.add(new PublicKey(ID_BASE + i, CRYPTO_TYPE, null, KEY_VALUE));
    }
    return publicKeys;
  }

  private Proof getProofForTest() {
    Proof proof = new Proof(CRYPTO_TYPE, Instant.now().truncatedTo(ChronoUnit.SECONDS),
        ID_BASE + random.nextInt(KEYS_COUNT), KEY_VALUE, null, null);
    return proof;
  }
}
