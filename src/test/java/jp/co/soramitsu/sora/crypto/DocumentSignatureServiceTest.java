package jp.co.soramitsu.sora.crypto;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import jp.co.soramitsu.sora.crypto.DocumentSignatureService.CreateVerifyHashException;
import jp.co.soramitsu.sora.crypto.DocumentSignatureService.NoSuchStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.hash.RawDigestStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DocumentSignatureServiceTest {

  private final String created = "2010-01-01T19:43:24Z";
  private final String signatureSuiteName = "Ed25519Sha3SignatureMock";

  private final RawDigestStrategy digest = mock(RawDigestStrategy.class);
  private final RawSignatureStrategy signatureStrategy = mock(RawSignatureStrategy.class);
  private final ObjectMapper mapper = mock(ObjectMapper.class);
  private final DocumentSignatureService documentSignatureService = spy(
      new DocumentSignatureService(digest, mapper));

  private VerifiableJson json = mock(VerifiableJson.class);
  private ProofProxy proofProxy = mock(ProofProxy.class);

  private final byte[] jsonHash = new byte[]{1, 1, 1};
  private final byte[] jsonSignature = new byte[]{2, 2, 2};

  private final PublicKey publicKey = mock(PublicKey.class);
  private final PrivateKey privateKey = mock(PrivateKey.class);
  private final KeyPair keyPair = new KeyPair(publicKey, privateKey);

  @Before
  public void setUp() throws SignatureSuiteException, NoSuchStrategy {
    when(proofProxy.getCreated())
        .thenReturn(Instant.parse(created));

    when(proofProxy.getType())
        .thenReturn(signatureSuiteName);

    doReturn(signatureStrategy).when(documentSignatureService)
        .getSignatureStrategy(signatureSuiteName);
    // when proof.setSignatureValue(bytes) is called, proof.getSignatureBytes() will return `bytes`
    doAnswer(i -> when(proofProxy.getSignatureValue()).thenReturn(jsonSignature))
        .when(proofProxy)
        .setSignatureValue(jsonSignature);

    when(digest.digest(any()))
        .thenReturn(jsonHash);

    when(signatureStrategy.generateKeypair())
        .thenReturn(keyPair);

    when(signatureStrategy.rawSign(
        jsonHash,
        keyPair
        )
    ).thenReturn(jsonSignature);

    when(signatureStrategy.rawVerify(
        jsonHash,
        jsonSignature,
        publicKey
        )
    ).thenReturn(Boolean.TRUE);
  }


  /**
   * Sign then verify predefined data.
   */
  @Test
  public void signThenVerify()
      throws CreateVerifyHashException, SignatureSuiteException, NoSuchStrategy {

    // Do the actual signing. This line modifies `json`.
    documentSignatureService.sign(json, keyPair, proofProxy);
    when(json.getProof()).thenReturn(proofProxy);
    // Do the actual verification.
    boolean verified = documentSignatureService.verify(json, keyPair.getPublic(), proofProxy);
    verify(proofProxy).setSignatureValue(jsonSignature);
    assertTrue(verified);
  }
}
