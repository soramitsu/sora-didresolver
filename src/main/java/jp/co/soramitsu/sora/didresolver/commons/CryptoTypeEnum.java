package jp.co.soramitsu.sora.didresolver.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * Possible algorithms for installing and verifying the signature
 */
public enum CryptoTypeEnum {
  Ed25519Sha3;

  /**
   * Get various crypto types for given crypto action
   */
  public static List<String> getCryptoTypes(CryptoActionTypeEnum actionTypeEnum) {
    List<String> cryptoTypes = new ArrayList<>();
    for (CryptoTypeEnum cryptoType : values()) {
      cryptoTypes.add(cryptoType + actionTypeEnum.getSuffix());
    }
    return cryptoTypes;
  }
}
