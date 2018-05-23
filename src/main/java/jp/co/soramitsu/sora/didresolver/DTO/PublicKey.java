package jp.co.soramitsu.sora.didresolver.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author rogachevsn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
