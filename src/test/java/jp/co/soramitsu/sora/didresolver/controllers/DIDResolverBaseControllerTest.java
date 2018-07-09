package jp.co.soramitsu.sora.didresolver.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import jp.co.soramitsu.sora.crypto.Consts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultMatcher;

@RunWith(SpringRunner.class)
@WebMvcTest(DIDResolverBaseController.class)
public class DIDResolverBaseControllerTest extends DIDResolverControllerInitializer {

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

  @Test
  public void testInvalidProofExceptionOnCreateDDO() throws Exception {
    when(validateService
        .isProofCreatorInAuth(ddo.getProof().get(0).getCreator(), ddo.getAuthentication()))
        .thenReturn(false);
    postRequest(status().isBadRequest());

    when(validateService
        .isProofInPublicKeys(any(), any()))
        .thenReturn(false);
    postRequest(status().isBadRequest());

    when(validateService
        .isProofCreatorInAuth(ddo.getProof().get(0).getCreator(), ddo.getAuthentication()))
        .thenReturn(true);
    postRequest(status().isBadRequest());
  }

  @Test
  public void testBadProofExceptionOnCreateDDO() throws Exception {
    when(cryptoService.verifyDDOProof(any(), any())).thenReturn(false);
    postRequest(status().isUnauthorized());
  }

  @Test
  public void testGetPublicKeysFromAnotherDDO() throws Exception {
    ddo.setId("did:sora:iroha:sergey@soramitsu.co.jp");
    String creator = ddo.getProof().get(0).getCreator();
    String proofCreatorDID = creator.substring(0, creator.indexOf(Consts.DID_URI_DETERMINATOR));
    given(storageService.read(proofCreatorDID)).willReturn(Optional.of(ddo));
    postRequest(status().isOk());
  }

  private void postRequest(ResultMatcher expectedStatus) throws Exception {
    mvc.perform(post(DIDResolverBaseController.PATH).contentType(contentType)
        .content(json.write(ddo).getJson())).andExpect(expectedStatus);
  }
}
