package jp.co.soramitsu.sora.didresolver.domain.iroha.entities;

import static javax.persistence.FetchType.LAZY;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

/**
 * This is an entity to which will be mapped a single row from table 'account' in Iroha's Postgres
 * database Fields are defined lazy explicitly, as usually not every single one of them is required
 * at the moment For such cases when multiple fields required there should be declared query in
 * {@link jp.co.soramitsu.sora.didresolver.domain.iroha.repositories.AccountRepository} which will return
 * projection of this entity with all required fields defined in {@link
 * javax.persistence.EntityGraph}
 *
 * @implNote This entity does not represent table 'account' and it's associated entities fully
 * due to specific case for which we don't need full model
 *
 */
@Entity
@Table(name = "account")
@Data
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Account {

  @Id
  @Column(name = "account_id", length = 288)
  private String accountId;
  @Basic(fetch = LAZY)
  @Column(name = "domain_id", length = 255)
  private String domainId;
  @Basic(fetch = LAZY)
  @Column(name = "data", columnDefinition = "jsonb")
  @Type(type = "jsonb")
  private AccountVault data;

}
