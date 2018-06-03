package jp.co.soramitsu.sora.didresolver.dto;

import java.net.URI;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {

  @NotBlank
  private String type;

  @NotNull
  private URI serviceEndpoint;
}
