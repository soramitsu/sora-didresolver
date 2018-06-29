package jp.co.soramitsu.sora.didresolver.dto;

import java.net.URI;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.validation.constraints.NotBlank;
import jp.co.soramitsu.sora.didresolver.dto.serializers.HexValueCombinedSerializer.HexValueDeserializer;
import jp.co.soramitsu.sora.didresolver.dto.serializers.HexValueCombinedSerializer.HexValueSerializer;
import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.KeyConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicKey {

  @KeyConstraint
  private URI id;

  @NotBlank
  @CryptoTypeConstraint
  private String type;

  @DIDConstraint(isNullable = true)
  private String owner;

  @JsonSerialize(using = HexValueSerializer.class)
  @JsonDeserialize(using = HexValueDeserializer.class)
  private byte[] publicKeyValue;
}
