package jp.co.soramitsu.sora.didresolver.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import javax.validation.constraints.NotBlank;
import jp.co.soramitsu.sora.didresolver.commons.CryptoActionTypeEnum;
import jp.co.soramitsu.sora.didresolver.dto.serializers.HexValueCombinedSerializer.HexValueDeserializer;
import jp.co.soramitsu.sora.didresolver.dto.serializers.HexValueCombinedSerializer.HexValueSerializer;
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
  @CryptoTypeConstraint(cryptoTypeEnum = CryptoActionTypeEnum.AUTH)
  private String type;

  @KeyConstraint
  private URI publicKey;

  @DIDConstraint(isNullable = true)
  private String owner;
  @JsonSerialize(using = HexValueSerializer.class)
  @JsonDeserialize(using = HexValueDeserializer.class)
  private byte[] publicKeyHex;
}
