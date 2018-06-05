package jp.co.soramitsu.sora.didresolver.configurations;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "jp.co.soramitsu.sora.didresolver.domain.iroha.repositories",
    entityManagerFactoryRef = "irohaEmf",
    transactionManagerRef = "irohaTxMgr"
)
public class IrohaDataAccessConfiguration {

  @Bean
  @ConfigurationProperties("iroha.datasource")
  public DataSourceProperties irohaDsProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("iroha.datasource")
  public DataSource irohaDS() {
    return irohaDsProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean irohaEmf(@Qualifier("irohaDS") DataSource irohaDS, EntityManagerFactoryBuilder builder) {
    return builder.dataSource(irohaDS).packages("jp.co.soramitsu.sora.didresolver.domain.iroha.entities").build();
  }

  @Bean
  public PlatformTransactionManager irohaTxMgr(@Qualifier("irohaEmf") EntityManagerFactory irohaEmf) {
    val txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(irohaEmf);

    return txManager;
  }

}
