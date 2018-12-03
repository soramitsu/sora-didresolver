package jp.co.soramitsu.sora.didresolver.controllers.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class GenericResponse {

  @Setter(PRIVATE)
  Status status;

  @Data
  @Setter(PRIVATE)
  @AllArgsConstructor
  @NoArgsConstructor
  @FieldDefaults(level = PRIVATE)
  public static class Status {

    ResponseCode code;
    String message;
  }
}
