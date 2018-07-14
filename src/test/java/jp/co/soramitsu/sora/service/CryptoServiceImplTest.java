package jp.co.soramitsu.sora.service;

import static jp.co.soramitsu.sora.util.DataProvider.ID_BASE;
import static jp.co.soramitsu.sora.util.DataProvider.KEYS_COUNT;
import static jp.co.soramitsu.sora.util.DataProvider.KEY_VALUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import jp.co.soramitsu.sora.crypto.Crypto.NoSuchStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;
import jp.co.soramitsu.sora.didresolver.services.CryptoService;
import jp.co.soramitsu.sora.didresolver.services.impl.CryptoServiceImpl;
import jp.co.soramitsu.sora.util.DataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CryptoServiceImplTest {

  private static final String DDO_JSON_NAME = "canonicalDDO.json";

  @Autowired
  private CryptoService cryptoService;

  private DataProvider dataProvider = new DataProvider();

  @TestConfiguration
  static class CryptoServiceImplTestContextConfiguration {

    @Bean
    public CryptoService cryptoService() {
      return new CryptoServiceImpl();
    }
  }

  @Test
  public void testSuccessGetPublicKeyByProof() {
    Optional<PublicKey> publicKey = cryptoService
        .getPublicKeyByProof(dataProvider.getProofForTest(), dataProvider.getPublicKeysForTest());
    assertTrue(publicKey.isPresent());
  }

  @Test
  public void testFailedGetPublicKeyByProof() {
    Proof proof = dataProvider.getProofForTest();
    proof.setCreator(URI.create(ID_BASE + KEYS_COUNT + 2));
    List<PublicKey> publicKeys = dataProvider.getPublicKeysForTest();
    Optional<PublicKey> publicKey = cryptoService
        .getPublicKeyByProof(proof, publicKeys);
    assertFalse(publicKey.isPresent());
  }

  @Test
  public void testSuccessVerifyDDOProof()
      throws SignatureSuiteException, IOException, NoSuchStrategy {
    assertTrue(cryptoService.verifyDDOProof(dataProvider.getDDOFromJson(DDO_JSON_NAME), KEY_VALUE));
  }

  @Test
  public void testFailedVerifyDDOProof()
      throws SignatureSuiteException, IOException, NoSuchStrategy {
    DDO ddo = dataProvider.getDDOFromJson(DDO_JSON_NAME);
    ddo.setCreated(Instant.now().truncatedTo(ChronoUnit.SECONDS));
    assertFalse(cryptoService.verifyDDOProof(ddo, KEY_VALUE));
  }
}
