package jp.co.soramitsu.sora.didresolver.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
import org.springframework.test.web.servlet.ResultMatcher;

@RunWith(SpringRunner.class)
@WebMvcTest(DIDResolverBaseController.class)
public class DIDResolverBaseControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private StorageService storageService;

  @Autowired
  private ObjectMapper objectMapper;

  private JacksonTester<DDO> json;

  private DDO ddo;

  private MediaType contentType = MediaType.APPLICATION_JSON_UTF8;

  @Before
  public void setUp() throws IOException {
    JacksonTester.initFields(this, objectMapper);
    Reader jsonReader = new BufferedReader(
        new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ddo.json")));
    ddo = json.read(jsonReader).getObject();
  }

  @Test
  public void testCreateDDO() throws Exception {
    postRequest(status().isOk());
  }

  @Test
  public void testDuplicateDDO() throws Exception {
    given(storageService.read(ddo.getId())).willReturn(Optional.of(ddo));
    postRequest(status().isUnprocessableEntity());
  }

  @Test
  public void testCheckDIDOnCreateDDO() throws Exception {
    ddo.setId(null);
    postRequest(status().isBadRequest());
  }

  @Test
  public void testCheckPublicKeyOnCreateDDO() throws Exception {
    ddo.setPublicKey(null);
    postRequest(status().isBadRequest());
  }

  @Test
  public void testCheckAuthenticationOnCreateDDO() throws Exception {
    ddo.setAuthentication(null);
    postRequest(status().isBadRequest());
  }

  @Test
  public void testCheckProofOnCreateDDO() throws Exception {
    ddo.setProof(null);
    postRequest(status().isBadRequest());
  }

  private void postRequest(ResultMatcher expectedStatus) throws Exception {
    mvc.perform(post(DIDResolverBaseController.PATH).contentType(contentType)
        .content(json.write(ddo).getJson())).andExpect(expectedStatus);
  }
}
