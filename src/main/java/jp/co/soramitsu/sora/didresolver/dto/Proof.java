package jp.co.soramitsu.sora.didresolver.dto;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.KeyConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proof {

  @NotBlank
  @CryptoTypeConstraint
  private String type;

  @NotNull
  private Date created;

  @NotBlank
  @KeyConstraint
  private String creator;

  private String signatureValueBase58;

  private String signatureValueHex;

  private String nonce;

  private String purpose;
}
