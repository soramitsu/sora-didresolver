package jp.co.soramitsu.sora.crypto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import jp.co.soramitsu.sora.crypto.Crypto.CreateVerifyHashException;
import jp.co.soramitsu.sora.crypto.Crypto.InvalidAlgorithmException;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.algorithms.SignatureSuiteRegistry;
import jp.co.soramitsu.sora.crypto.hash.RawDigestStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CryptoTest {

  final RawDigestStrategy digest = mock(RawDigestStrategy.class);
  final RawSignatureStrategy signatureStrategy = mock(RawSignatureStrategy.class);
  final Crypto crypto = new Crypto(digest);
  final String created = "2010-01-01T19:43:24Z";

  VerifiableJson json = mock(VerifiableJson.class);
  ProofProxy proofProxy = mock(ProofProxy.class);

  final byte[] jsonHash = new byte[]{1, 1, 1};
  final byte[] jsonSignature = new byte[]{2, 2, 2};

  final PublicKey publicKey = mock(PublicKey.class);
  final PrivateKey privateKey = mock(PrivateKey.class);
  final KeyPair keyPair = new KeyPair(publicKey, privateKey);

  final String signatureSuiteName = "Ed25519Sha3SignatureMock";

  List<ProofProxy> proofs = new LinkedList<>();

  CryptoTest() {
  }

  @BeforeEach
  void setUp() throws SignatureSuiteException {
    when(proofProxy.getCreated())
        .thenReturn(Instant.parse(created));

    SignatureSuiteRegistry
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
    ).thenReturn(true);

  }


  /**
   * Sign then verify predefined data.
   */
  @Test
  void signThenVerify()
      throws InvalidAlgorithmException, CreateVerifyHashException, SignatureSuiteException {

    when(json.serializeAsMap())
        .thenReturn(ImmutableMap.of(
            "keyInsideDocument", "321"
        ));

    when(proofProxy.serializeAsMap())
        .thenReturn(ImmutableMap.of(
            "keyInsideProof", "123"
        ));

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
