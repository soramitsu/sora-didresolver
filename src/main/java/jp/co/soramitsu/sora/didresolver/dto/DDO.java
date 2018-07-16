package jp.co.soramitsu.sora.didresolver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.crypto.VerifiableJson;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DDO implements VerifiableJson<Proof> {

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

  @DIDConstraint(isNullable = true)
  private String guardian;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private Instant created;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private Instant updated;

  @NotNull
  @Valid
  private Proof proof;
}
