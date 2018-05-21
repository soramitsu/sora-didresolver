package jp.co.soramitsu.sora.didresolver.DAO;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author rogachevsn
 */
@Data
@Entity
public class Proof {

    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private Date created;

    private String creator;

    private String signatureValue;
}
