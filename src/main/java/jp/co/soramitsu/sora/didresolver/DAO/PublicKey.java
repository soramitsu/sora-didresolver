package jp.co.soramitsu.sora.didresolver.DAO;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;

/**
 * @author rogachevsn
 */
@Data
@Entity
public class PublicKey {

    @Id
    private String id;

    private String type;

    private String owner;

    private String publicKeyPem;

    private String publicKeyBase58;
}
