package jp.co.soramitsu.sora.didresolver.dto;

import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import jp.co.soramitsu.sora.didresolver.validation.constrains.ExactlyOneConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ExactlyOneConstraint(group = {"signatureValueBase58", "signatureValueHex"})
class Proof {

    @NotBlank
    @CryptoTypeConstraint
    private String type;

    @NotNull
    private Date created;

    @NotBlank
    private String creator;

    private String signatureValueBase58;

    private String signatureValueHex;

    private String nonce;

    private String purpose;
}
