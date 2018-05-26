package jp.co.soramitsu.sora.didresolver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author rogachevsn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proof {

    @NotBlank
    private String type;

    @NotBlank
    private Date created;

    @NotBlank
    private String creator;

    private String signatureValueBase58;

    private String signatureValueHex;

    private String nonce;

    private String purpose;
}
