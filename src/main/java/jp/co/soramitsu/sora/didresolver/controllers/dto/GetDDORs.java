package jp.co.soramitsu.sora.didresolver.controllers.dto;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetDDORs extends SuccessfulResponse {

  private JsonNode ddo;
}
