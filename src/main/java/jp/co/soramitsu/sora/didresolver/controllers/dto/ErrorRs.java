package jp.co.soramitsu.sora.didresolver.controllers.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Defines the way in which a client will received Response in case of business errors")
public class ErrorRs {

  private String error;

  public enum BusinessErrors {
    DID_DUPLICATE,
    UNKNOWN
  }
}
