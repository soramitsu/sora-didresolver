package jp.co.soramitsu.sora.didresolver.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author rogachevsn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authentication {

    @NotBlank
    private String type;

    private String publicKey;

    private String owner;

    private String publicKeyHex;
}
