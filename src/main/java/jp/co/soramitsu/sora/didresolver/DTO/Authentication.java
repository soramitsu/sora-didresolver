package jp.co.soramitsu.sora.didresolver.dto;

import javax.validation.constraints.NotBlank;
import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authentication {

  @NotBlank
  @CryptoTypeConstraint
  private String type;

  private String publicKey;

  @DIDConstraint(isNullable = true)
  private String owner;

  private String publicKeyHex;
}
