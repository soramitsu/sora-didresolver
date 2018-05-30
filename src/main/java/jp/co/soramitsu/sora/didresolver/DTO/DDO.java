package jp.co.soramitsu.sora.didresolver.dto;

import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * @author rogachevsn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DDO {

    @NotBlank
    @DIDConstraint(isNullable = false)
    private String id;

    @NotNull
    @Valid
    private PublicKey[] publicKey;

    @NotNull
    @Valid
    private Authentication[] authentication;

    @DIDConstraint(isNullable = true)
    private String owner;

    private Service[] service;

    @DIDConstraint(isNullable = true)
    private String guardian;

    private Date created;

    private Date updated;

    @NotNull
    @Valid
    private Proof[] proof;
}
