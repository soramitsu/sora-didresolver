package jp.co.soramitsu.sora.didresolver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.crypto.ProofProxy;
import jp.co.soramitsu.sora.didresolver.commons.CryptoActionTypeEnum;
import jp.co.soramitsu.sora.didresolver.dto.serializers.HexValueCombinedSerializer.HexValueDeserializer;
import jp.co.soramitsu.sora.didresolver.dto.serializers.HexValueCombinedSerializer.HexValueSerializer;
import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.KeyConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proof implements ProofProxy {

  @NotBlank
  @CryptoTypeConstraint(cryptoTypeEnum = CryptoActionTypeEnum.SIGNATURE)
  private String type;

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private Instant created;

  @KeyConstraint
  private URI creator;

  @JsonSerialize(using = HexValueSerializer.class)
  @JsonDeserialize(using = HexValueDeserializer.class)
  private byte[] signatureValue;

  @NotBlank
  private String nonce;

  private String purpose;
}
