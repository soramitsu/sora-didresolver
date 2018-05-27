package jp.co.soramitsu.sora.didresolver.dto;

import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.ExactlyOneConstraint;
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
@ExactlyOneConstraint(group = {"publicKeyPem", "publicKeyBase58", "publicKeyHex"})
class PublicKey {

    @NotBlank
    private String id;

    @NotBlank
    @CryptoTypeConstraint
    private String type;

    @DIDConstraint(isNullable = true)
    private String owner;

    private String publicKeyPem;

    private String publicKeyBase58;

    private String publicKeyHex;
}
