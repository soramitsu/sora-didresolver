package jp.co.soramitsu.sora.didresolver.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CryptoActionTypeEnum {
  VERIFY("VerificationKey"),
  AUTH("Authentication"),
  SIGNATURE("Signature");

  @Getter
  private String suffix;
}
