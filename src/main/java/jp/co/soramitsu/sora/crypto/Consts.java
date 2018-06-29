package jp.co.soramitsu.sora.crypto;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Consts {

  public final String PROOF_KEY = "proof";
  public final String SIGNATURE_KEY = "signature";

  public final String ED25519_SHA3_SIGNATURE_SUITE = "Ed25519Sha3Signature";
  public final String ECDSAWITHSHA256_SIGNATURE_SUITE = "EcdsaWithSha256Signature";

  public final char DID_URI_DETERMINATOR = '#';
}
