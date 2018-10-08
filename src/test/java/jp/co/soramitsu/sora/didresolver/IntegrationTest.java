package jp.co.soramitsu.sora.didresolver;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.jayway.jsonpath.JsonPath.read;
import static java.util.Collections.singletonList;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "logging.level.jp.co.soramitsu.sora=debug"
    },
    classes = {DIDResolverApplication.class})
@Configuration
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class IntegrationTest {

  public static final String PUBLICKEY =
      "313a07e6384776ed95447710d15e59148473ccfc052a681317a72a69f2a49910";
  public static final String PRIVATEKEY =
      "f101537e319568c765b2cc89698325604991dca57b9716b58016b253506cab70";
  protected static final String TOKEN = "$.token";

  static {
    System.setProperty("DIDRESOLVER_IROHA_PUBLIC_KEY", PUBLICKEY);
    System.setProperty("DIDRESOLVER_IROHA_PRIVATE_KEY", PRIVATEKEY);
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  protected TestRestTemplate testRestTemplate;

  protected <T> HttpEntity<T> createHttpEntity(ResponseEntity<String> response, T body) {
    String token = read(response.getBody(), TOKEN);
    return createHttpEntity(token, body);
  }

  protected <T> HttpEntity<T> createHttpEntity(String token, T body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(singletonList(MediaType.APPLICATION_JSON_UTF8));
    if (token != null) {
      headers.set(AUTHORIZATION, "Bearer " + token);
    }
    return new HttpEntity<>(body, headers);
  }
}
