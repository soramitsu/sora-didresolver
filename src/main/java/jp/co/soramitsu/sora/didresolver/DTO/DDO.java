package jp.co.soramitsu.sora.didresolver.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String id;

    @NotNull
    private PublicKey[] publicKey;

    @NotNull
    private Authentication[] authentication;

    private String owner;

    private String guardian;

    private Date created;

    private Date updated;

    private Proof proof;

    public String getId(){
        return id;
    }
}
