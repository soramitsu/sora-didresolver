package jp.co.soramitsu.sora.didresolver.controllers;

import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.ID_PARAM;
import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.PATH;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import jp.co.soramitsu.sora.didresolver.controllers.dto.GenericResponse;
import jp.co.soramitsu.sora.didresolver.controllers.dto.GetDDORs;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class Requests {

  private static final String PATH_WITH_ID = PATH + ID_PARAM;

  private final TestRestTemplate testRestTemplate;

  Requests(TestRestTemplate testRestTemplate) {
    this.testRestTemplate = testRestTemplate;
  }

  /**
   * GET DDO
   */
  ResponseEntity<GetDDORs> getDDO(DID did) {
    return testRestTemplate.getForEntity(PATH_WITH_ID, GetDDORs.class, did);
  }

  /**
   * CREATE DDO
   */
  ResponseEntity<GenericResponse> createDDO(DDO ddo) {
    return testRestTemplate.postForEntity(PATH, ddo, GenericResponse.class);
  }

  /**
   * UPDATE DDO
   */
  ResponseEntity<GenericResponse> updateDDO(DID did, DDO ddo) {
    return testRestTemplate.exchange(
        PATH_WITH_ID,
        PUT,
        createHttpEntity(ddo),
        GenericResponse.class,
        did);
  }

  /**
   * DELETE DDO
   */
  ResponseEntity<GenericResponse> deleteDDO(DID did) {
    return testRestTemplate.exchange(
        PATH_WITH_ID,
        DELETE,
        createHttpEntity(null),
        GenericResponse.class,
        did.toString());
  }

  private <T> HttpEntity<T> createHttpEntity(T body) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(APPLICATION_JSON_UTF8);

    return new HttpEntity<>(body, httpHeaders);
  }
}
