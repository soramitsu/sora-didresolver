package jp.co.soramitsu.sora.didresolver.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

@RunWith(SpringRunner.class)
@WebMvcTest(DIDResolverController.class)
public class DIDResolverControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private StorageService storageService;

  @Autowired
  private ObjectMapper objectMapper;

  private JacksonTester<DDO> json;

  private DDO ddo;

  private MediaType contentType = MediaType.APPLICATION_JSON_UTF8;

  private static final String URL =
      DIDResolverBaseController.PATH + DIDResolverBaseController.ID_PARAM;

  private static final String INCORRECT_DID = "did:sora:iroha:5LkqENiDNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn";

  @Before
  public void setUp() throws IOException {
    JacksonTester.initFields(this, objectMapper);
    Reader jsonReader = new BufferedReader(
        new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ddo.json")));
    ddo = json.read(jsonReader).getObject();
  }

  @Test
  public void testGetDDO() throws Exception {
    given(storageService.read(ddo.getId())).willReturn(Optional.of(ddo));
    sendDDORequest(get(URL, ddo.getId()), status().isOk(), content().contentType(contentType),
        content().json(json.write(ddo).getJson()));
  }

  @Test
  public void testDIDNotFoundOnGetDDO() throws Exception {
    sendDDORequest(get(URL, ddo.getId()), status().isNotFound());
  }

  @Test
  public void testIncorrectDIDOnGetDDO() throws Exception {
    // set incorrect did for check validation
    ddo.setId(INCORRECT_DID);
    sendDDORequest(get(URL, ddo.getId()), status().isBadRequest());
  }

  @Test
  public void testDeleteDDO() throws Exception {
    given(storageService.read(ddo.getId())).willReturn(Optional.of(ddo));
    sendDDORequest(delete(URL, ddo.getId()), status().isOk());
  }

  @Test
  public void testDIDNotFoundOnDeleteDDO() throws Exception {
    sendDDORequest(delete(URL, ddo.getId()), status().isNotFound());
  }

  @Test
  public void testIncorrectDIDOnDeleteDDO() throws Exception {
    // set incorrect did for check validation
    ddo.setId(INCORRECT_DID);
    sendDDORequest(delete(URL, ddo.getId()), status().isBadRequest());
  }

  @Test
  public void testUpdateDDO() throws Exception {
    given(storageService.read(ddo.getId())).willReturn(Optional.of(ddo));
    ddo.setCreated(Date.from(Instant.now()));
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isOk());
  }

  @Test
  public void testDIDNotFoundOnUpdateDDO() throws Exception {
    ddo.setCreated(Date.from(Instant.now()));
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isNotFound());
  }

  @Test
  public void testIncorrectDIDOnUpdateDDO() throws Exception {
    // set incorrect did for check validation
    ddo.setId(INCORRECT_DID);
    ddo.setCreated(Date.from(Instant.now()));
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testCheckPublicKeyOnUpdateDDO() throws Exception {
    ddo.setPublicKey(null);
    ddo.setCreated(Date.from(Instant.now()));
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testCheckAuthenticationOnUpdateDDO() throws Exception {
    ddo.setAuthentication(null);
    ddo.setCreated(Date.from(Instant.now()));
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testCheckProofOnUpdateDDO() throws Exception {
    ddo.setProof(null);
    ddo.setCreated(Date.from(Instant.now()));
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  private void sendDDORequest(RequestBuilder builder, ResultMatcher... expectedResults)
      throws Exception {
    ResultActions resultActions = mvc.perform(builder);
    for (ResultMatcher expectedResult : expectedResults) {
      resultActions.andExpect(expectedResult);
    }
  }
}
