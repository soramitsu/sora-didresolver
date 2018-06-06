package jp.co.soramitsu.sora.didresolver.dto;

import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DDO {

  @NotBlank
  @DIDConstraint(isNullable = false)
  private String id;

  @NotNull
  @Valid
  private List<PublicKey> publicKey;

  @NotNull
  @Valid
  private List<Authentication> authentication;

  @DIDConstraint(isNullable = true)
  private String owner;

  private List<Service> service;

  @DIDConstraint(isNullable = true)
  private String guardian;

  private Date created;

  private Date updated;

  @NotNull
  @Valid
  private List<Proof> proof;
}
