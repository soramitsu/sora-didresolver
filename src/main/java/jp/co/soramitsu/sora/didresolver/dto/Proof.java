package jp.co.soramitsu.sora.didresolver.dto;

import java.net.URI;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.crypto.ProofProxy;
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
  @CryptoTypeConstraint
  private String type;

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private Instant created;

  @KeyConstraint
  private URI creator;

  @JsonSerialize(using = HexValueSerializer.class)
  @JsonDeserialize(using = HexValueDeserializer.class)
  private byte[] signatureValue;

  private String nonce;

  private String purpose;
}
