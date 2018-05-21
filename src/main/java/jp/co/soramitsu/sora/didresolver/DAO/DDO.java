package jp.co.soramitsu.sora.didresolver.DAO;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


/**
 * @author rogachevsn
 */
@Data
@Entity
public class DDO {

    @Id
    private String id;

    private PublicKey[] keys;

    private Authentication auth;

    private Date created;

    private Proof proof;
}
