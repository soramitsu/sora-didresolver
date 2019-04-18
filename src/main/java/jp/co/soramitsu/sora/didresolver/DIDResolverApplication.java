package jp.co.soramitsu.sora.didresolver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DIDResolverApplication {

  public static void main(String[] args) {

    SpringApplication.run(DIDResolverApplication.class, args);
  }
}
