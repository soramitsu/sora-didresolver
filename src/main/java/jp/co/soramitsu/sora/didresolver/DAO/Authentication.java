package jp.co.soramitsu.sora.didresolver.DAO;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author rogachevsn
 */
@Data
@Entity
public class Authentication {

    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private String publicKeyId;
}
