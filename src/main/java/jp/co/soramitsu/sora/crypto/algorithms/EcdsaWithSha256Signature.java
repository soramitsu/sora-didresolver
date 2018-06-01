package jp.co.soramitsu.sora.crypto.algorithms;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import jp.co.soramitsu.sora.crypto.Consts;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;

public class EcdsaWithSha256Signature implements RawSignatureStrategy {

  // enable spongycastle
  static {
    Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
  }

  private static final String cryptoProvider = "SC";

  @Override
  public byte[] rawSign(byte[] data, KeyPair keyPair) throws SignatureSuiteException {
    try {
      Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", cryptoProvider);
      ecdsaSign.initSign(keyPair.getPrivate());
      ecdsaSign.update(data);
      return ecdsaSign.sign();
    } catch (Exception e) {
      throw new SignatureSuiteException(e);
    }
  }

  @Override
  public boolean rawVerify(byte[] data, byte[] signature, PublicKey publicKey)
      throws SignatureSuiteException {

    try {
      Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", cryptoProvider);
      ecdsaVerify.initVerify(publicKey);
      ecdsaVerify.update(data);
      return ecdsaVerify.verify(signature);
    } catch (Exception e) {
      throw new SignatureSuiteException(e);
    }
  }

  @Override
  public KeyPair generateKeypair() throws SignatureSuiteException {
    try {
      ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("B-571");
      KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", cryptoProvider);
      g.initialize(ecSpec, new SecureRandom());
      return g.generateKeyPair();
    } catch (Exception e) {
      throw new SignatureSuiteException(e);
    }
  }

  @Override
  public String getType() {
    return Consts.ECDSAWITHSHA256_SIGNATURE_SUITE;
  }
}
