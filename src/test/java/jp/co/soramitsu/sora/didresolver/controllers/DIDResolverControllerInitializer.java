package jp.co.soramitsu.sora.didresolver.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public abstract class DIDResolverControllerInitializer {

  @Autowired
  protected MockMvc mvc;
  @MockBean
  protected StorageService storageService;
  @MockBean
  protected VerifyService verifyService;
  protected JacksonTester<DDO> json;
  protected DDO ddo;
  protected MediaType contentType = APPLICATION_JSON_UTF8;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() throws IOException {
    JacksonTester.initFields(this, objectMapper);
    Reader jsonReader = new BufferedReader(
        new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ddo.json")));
    ddo = json.read(jsonReader).getObject();
    Proof proof = ddo.getProof();
    when(verifyService.isCreatorInAuth(proof.getOptions().getCreator(), ddo.getAuthentication()))
        .thenReturn(true);
    when(verifyService.isCreatorInPublicKeys(proof.getOptions().getCreator(), ddo.getPublicKey()))
        .thenReturn(true);
  }
}
