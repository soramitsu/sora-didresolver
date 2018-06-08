package jp.co.soramitsu.sora.crypto.algorithms;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import jp.co.soramitsu.crypto.ed25519.Utils;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Ed25519Sha3SignatureTest {

  private static final String fileName = "/crypto/test.data.sha3";
  private RawSignatureStrategy sig = new Ed25519Sha3Signature();
  private TestTuple testCase;

  public Ed25519Sha3SignatureTest(TestTuple tuple) throws SignatureSuiteException {
    this.testCase = tuple;
  }

  @Parameterized.Parameters
  public static List<TestTuple> parameters() throws IOException {
    List<TestTuple> testCases = new ArrayList<>();

    InputStream is = Ed25519Sha3SignatureTest.class.getResourceAsStream(fileName);
    if (is == null) {
      throw new IOException("Resource not found: " + fileName);
    }

    BufferedReader file = new BufferedReader(new InputStreamReader(is));
    String line;
    while ((line = file.readLine()) != null) {
      testCases.add(new TestTuple(line));
    }
    return testCases;
  }

  @Test
  public void rawSign() throws SignatureSuiteException {
    KeyPair keyPair = sig.generateKeypair(testCase.seed);
    byte[] signature = sig.rawSign(testCase.message, keyPair);

    assertArrayEquals(testCase.sig, signature);
  }

  @Test
  public void rawVerify() throws SignatureSuiteException {
    KeyPair keyPair = sig.generateKeypair(testCase.seed);

    assertTrue(
        sig.rawVerify(
            testCase.message,
            testCase.sig,
            keyPair.getPublic()
        )
    );
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
    KeyPair keyPair = sig.generateKeypair(testCase.seed);

    assertNotNull(keyPair);
    assertNotNull(keyPair.getPrivate());
    assertNotNull(keyPair.getPublic());
  }

  @Test
  public void getType() {
    assertEquals(
        "Ed25519Sha3Signature",
        sig.getType()
    );
  }

  public static class TestTuple {

    public TestTuple(String line) {
      String[] x = line.split(":");
      seed = Utils.hexToBytes(x[0].substring(0, 64)); // private key
      pk = Utils.hexToBytes(x[1]); // public key
      message = Utils.hexToBytes(x[2]);
      sig = Utils.hexToBytes(x[3].substring(0, 128)); // signature
    }

    private byte[] seed;
    private byte[] pk;
    private byte[] message;
    private byte[] sig;
  }
}
