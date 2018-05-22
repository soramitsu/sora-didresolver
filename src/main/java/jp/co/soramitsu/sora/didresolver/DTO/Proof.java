package jp.co.soramitsu.sora.didresolver.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author rogachevsn
 */
@Data
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
