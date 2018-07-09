package jp.co.soramitsu.sora.didresolver.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;
import jp.co.soramitsu.sora.didresolver.services.CryptoService;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.ValidateService;
import org.junit.Before;
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
  protected CryptoService cryptoService;

  @MockBean
  protected ValidateService validateService;

  @Autowired
  private ObjectMapper objectMapper;

  protected JacksonTester<DDO> json;

  protected DDO ddo;

  protected MediaType contentType = MediaType.APPLICATION_JSON_UTF8;

  @Before
  public void setUp() throws IOException {
    JacksonTester.initFields(this, objectMapper);
    Reader jsonReader = new BufferedReader(
        new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ddo.json")));
    ddo = json.read(jsonReader).getObject();
    Proof proof = ddo.getProof().get(0);
    PublicKey publicKey = ddo.getPublicKey().get(1);
    when(cryptoService.getPublicKeyByProof(proof, ddo.getPublicKey()))
        .thenReturn(Optional.of(publicKey));
    when(validateService.isProofCreatorInAuth(proof.getCreator(), ddo.getAuthentication()))
        .thenReturn(true);
    when(validateService.isProofInPublicKeys(proof.getCreator(), ddo.getPublicKey()))
        .thenReturn(true);
    when(cryptoService.verifyDDOProof(any(), any())).thenReturn(true);
  }
}
