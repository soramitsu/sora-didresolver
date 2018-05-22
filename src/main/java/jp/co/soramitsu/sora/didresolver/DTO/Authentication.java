package jp.co.soramitsu.sora.didresolver.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author rogachevsn
 */
@Data
public class Authentication {

    @NotBlank
    private String type;

    private String publicKey;

    private String owner;

    private String publicKeyHex;
}
