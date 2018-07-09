package jp.co.soramitsu.sora.crypto.hash;

import org.spongycastle.jcajce.provider.digest.SHA3;
import org.spongycastle.jcajce.provider.digest.SHA3.Digest256;

public class Sha3_256 implements RawDigestStrategy {

  private final SHA3.Digest256 hash = new Digest256();

  @Override
  public byte[] digest(byte[] input) {
    return hash.digest(input);
  }
}
