package jp.co.soramitsu.sora.didresolver.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author rogachevsn
 */
@Data
public class PublicKey {

    @NotBlank
    private String id;

    @NotBlank
    private String type;

    private String owner;

    private String publicKeyPem;

    private String publicKeyBase58;

    private String publicKeyHex;
}
