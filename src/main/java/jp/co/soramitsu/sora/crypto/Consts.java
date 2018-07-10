package jp.co.soramitsu.sora.crypto;

import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

@UtilityClass
@FieldDefaults(makeFinal = true)
public final class Consts {

  public String PROOF_KEY = "proof";
  public String SIGNATURE_KEY = "signature";

  public String ED25519_SHA3_SIGNATURE_SUITE = "Ed25519Sha3Signature";
  public String ECDSAWITHSHA256_SIGNATURE_SUITE = "EcdsaWithSha256Signature";
}
