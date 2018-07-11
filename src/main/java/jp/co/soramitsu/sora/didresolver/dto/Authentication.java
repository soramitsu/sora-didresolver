package jp.co.soramitsu.sora.didresolver.dto;

import java.net.URI;
import javax.validation.constraints.NotBlank;
import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.KeyConstraint;
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

  @KeyConstraint
  private URI publicKey;

  @DIDConstraint(isNullable = true)
  private String owner;

  private String publicKeyHex;
}
