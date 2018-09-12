package jp.co.soramitsu.sora.didresolver.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRs {

  private String error;

  public enum BusinessErrors {
    DID_DUPLICATE,
    UNKNOWN
  }
}
