package jp.co.soramitsu.sora.crypto.algorithms;

import static jp.co.soramitsu.crypto.ed25519.spec.EdDSANamedCurveTable.ED_25519;

import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import jp.co.soramitsu.crypto.ed25519.EdDSAEngine;
import jp.co.soramitsu.crypto.ed25519.EdDSAPrivateKey;
import jp.co.soramitsu.crypto.ed25519.EdDSAPublicKey;
import jp.co.soramitsu.crypto.ed25519.EdDSASecurityProvider;
import jp.co.soramitsu.crypto.ed25519.KeyPairGenerator;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSANamedCurveTable;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAParameterSpec;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAPrivateKeySpec;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAPublicKeySpec;
import jp.co.soramitsu.sora.crypto.Consts;

public class Ed25519Sha3Signature implements RawSignatureStrategy {

  static {
    Security.addProvider(new EdDSASecurityProvider());
  }

  private EdDSAParameterSpec spec;
  private Signature sgr;
  private KeyPairGenerator keyGen;


  public Ed25519Sha3Signature() throws SignatureSuiteException {
    try {
      this.spec = EdDSANamedCurveTable.getByName(ED_25519);
      this.keyGen = new KeyPairGenerator();
      this.sgr = new EdDSAEngine(
          MessageDigest.getInstance(
              spec.getHashAlgorithm()
          )
      );
    } catch (Exception e) {
      throw new SignatureSuiteException(e);
    }
  }


  @Override
  public byte[] rawSign(byte[] data, KeyPair keypair) throws SignatureSuiteException {
    try {
      sgr.initSign(keypair.getPrivate());
      sgr.update(data);
      return sgr.sign();
    } catch (Exception e) {
      throw new SignatureSuiteException(e);
    }
  }

  @Override
  public boolean rawVerify(byte[] data, byte[] signature, PublicKey publicKey)
      throws SignatureSuiteException {
    try {
      sgr.initVerify(publicKey);
      sgr.update(data);
      return sgr.verify(signature);
    } catch (Exception e) {
      throw new SignatureSuiteException(e);
    }
  }

  @Override
  public KeyPair generateKeypair() throws SignatureSuiteException {
    try {
      return keyGen.generateKeyPair();
    } catch (Exception e) {
      throw new SignatureSuiteException(e);
    }
  }

  @Override
  public KeyPair generateKeypair(byte[] seed) throws SignatureSuiteException {
    try {
      EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(seed, spec);
      EdDSAPublicKeySpec pubKey = new EdDSAPublicKeySpec(privKey.getA(), spec);

      return new KeyPair(
          new EdDSAPublicKey(pubKey),
          new EdDSAPrivateKey(privKey)
      );

    } catch (Exception e) {
      throw new SignatureSuiteException(e);
    }
  }

  @Override
  public String getType() {
    return Consts.ED25519_SHA3_SIGNATURE_SUITE;
  }
}
