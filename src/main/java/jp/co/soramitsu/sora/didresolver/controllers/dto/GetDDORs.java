package jp.co.soramitsu.sora.didresolver.controllers.dto;

import static lombok.AccessLevel.PRIVATE;

import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@Setter(PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = PRIVATE)
public class GetDDORs extends SuccessfulResponse {

  DDO ddo;
}
