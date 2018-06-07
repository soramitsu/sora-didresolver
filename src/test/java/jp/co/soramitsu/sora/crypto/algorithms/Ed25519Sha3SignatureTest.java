package jp.co.soramitsu.sora.crypto.algorithms;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.xml.bind.DatatypeConverter;
import jp.co.soramitsu.crypto.ed25519.EdDSAPublicKey;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import org.junit.Test;

public class Ed25519Sha3SignatureTest {

  private RawSignatureStrategy sig;

  public Ed25519Sha3SignatureTest() throws SignatureSuiteException {
    this.sig = new Ed25519Sha3Signature();
  }

  @Test
  public void rawSign() throws SignatureSuiteException {
    for (TestVectors.TestTuple testCase : TestVectors.testCases) {
      KeyPair keyPair = sig.generateKeypair(testCase.seed);
      byte[] signature = sig.rawSign(testCase.message, keyPair);

      assertArrayEquals(testCase.sig, signature);
    }
  }

  @Test
  public void rawVerify() throws SignatureSuiteException {
    for (TestVectors.TestTuple testCase : TestVectors.testCases) {
      KeyPair keyPair = sig.generateKeypair(testCase.seed);
      assertTrue(sig.rawVerify(testCase.message, testCase.sig, keyPair.getPublic()));
    }
  }

  @Test
  public void generateKeypair() throws SignatureSuiteException {
    KeyPair keyPair = sig.generateKeypair();
    assertNotNull(keyPair);
    assertNotNull(keyPair.getPublic());
    assertNotNull(keyPair.getPrivate());
  }

  @Test
  public void generateKeypairFromSeed() throws SignatureSuiteException {
    for (TestVectors.TestTuple testCase : TestVectors.testCases) {
      KeyPair keyPair = sig.generateKeypair(testCase.seed);

      assertNotNull(keyPair);
      assertNotNull(keyPair.getPrivate());
      assertNotNull(keyPair.getPublic());
    }
  }

  @Test
  public void getType() {
    assertEquals(
        "Ed25519Sha3Signature",
        sig.getType()
    );
  }
}
