package jp.co.soramitsu.sora.didresolver.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * @author rogachevsn
 */
@Data
public class DDO {

    @NotBlank
    private String id;

    @NotNull
    private PublicKey[] keys;

    @NotNull
    private Authentication auth;

    private String owner;

    private String guardian;

    private Date created;

    private Date updated;

    private Proof proof;
}
