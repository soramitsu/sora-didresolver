package jp.co.soramitsu.sora.crypto;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import jp.co.soramitsu.sora.crypto.Crypto.CreateVerifyHashException;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.algorithms.SignatureSuiteRegistry;
import jp.co.soramitsu.sora.crypto.algorithms.SignatureSuiteRegistry.NoSuchStrategy;
import jp.co.soramitsu.sora.crypto.hash.RawDigestStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CryptoTest {

  private final String created = "2010-01-01T19:43:24Z";
  private final String signatureSuiteName = "Ed25519Sha3SignatureMock";

  private final RawDigestStrategy digest = mock(RawDigestStrategy.class);
  private final RawSignatureStrategy signatureStrategy = mock(RawSignatureStrategy.class);
  private final ObjectMapper mapper = mock(ObjectMapper.class);
  private final Crypto crypto = new Crypto(digest, mapper);

  private VerifiableJson json = mock(VerifiableJson.class);
  private ProofProxy proofProxy = mock(ProofProxy.class);

  private final byte[] jsonHash = new byte[]{1, 1, 1};
  private final byte[] jsonSignature = new byte[]{2, 2, 2};

  private final PublicKey publicKey = mock(PublicKey.class);
  private final PrivateKey privateKey = mock(PrivateKey.class);
  private final KeyPair keyPair = new KeyPair(publicKey, privateKey);


  private List<ProofProxy> proofs = new LinkedList<>();

  @Before
  public void setUp() throws SignatureSuiteException {
    when(proofProxy.getCreated())
        .thenReturn(Instant.parse(created));

    SignatureSuiteRegistry.INSTANCE
        .register(signatureSuiteName, signatureStrategy);
    when(proofProxy.getType())
        .thenReturn(signatureSuiteName);

    // when proof.setSignatureValue(bytes) is called, proof.getSignatureBytes() will return `bytes`
    doAnswer(i -> when(proofProxy.getSignatureValue()).thenReturn(jsonSignature))
        .when(proofProxy)
        .setSignatureValue(jsonSignature);

    // setProof() inside tested class will save proof into `this.proofs`
    proofs.clear();
    doAnswer(inv -> proofs.addAll(inv.getArgument(0)))
        .when(json)
        .setProof(anyList());

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
    crypto.sign(json, keyPair, proofProxy);

    when(json.getProof())
        .thenReturn(this.proofs);

    // Do the actual verification.
    boolean verified = crypto
        .verifyAll(json, keyPair.getPublic());

    verify(proofProxy)
        .setSignatureValue(jsonSignature);

    assertTrue(verified);
  }
}
