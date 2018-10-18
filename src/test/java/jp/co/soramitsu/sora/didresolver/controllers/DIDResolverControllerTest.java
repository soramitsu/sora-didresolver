package jp.co.soramitsu.sora.didresolver.controllers;

import static java.time.Instant.now;
import static java.util.Optional.of;
import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.ID_PARAM;
import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.PATH;
import static jp.co.soramitsu.sora.sdk.did.model.dto.DID.parse;
import static jp.co.soramitsu.sora.sdk.did.model.dto.DID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

@RunWith(SpringRunner.class)
@WebMvcTest(DIDResolverController.class)
public class DIDResolverControllerTest extends DIDResolverControllerInitializer {

  private static final String URL =
      PATH + ID_PARAM;
  private static final String DEFAULT_LONG_DID = "did:sora:very:long:did:that:is:longer:that:toyota:century";

  @Test
  public void testGetDDO() throws Exception {
    given(storageService.findDDObyDID(ddo.getId().toString())).willReturn(of(ddo));
    sendDDORequest(get(URL, ddo.getId().toString()), status().isOk(),
        content().contentType(contentType),
        content().json(json.write(ddo).getJson()));
  }

  @Test
  public void testDIDNotFoundOnGetDDO() throws Exception {
    sendDDORequest(get(URL, ddo.getId().toString()), status().isNotFound());
  }

  @Test
  public void testDeleteDDO() throws Exception {
    given(storageService.findDDObyDID(ddo.getId().toString())).willReturn(of(ddo));
    sendDDORequest(delete(URL, ddo.getId().toString()), status().isOk());
  }

  @Test
  public void testDIDNotFoundOnDeleteDDO() throws Exception {
    doThrow(new DIDNotFoundException(ddo.getId().toString())).when(storageService)
        .delete(ddo.getId().toString());
    sendDDORequest(delete(URL, ddo.getId().toString()), status().isNotFound());
  }

  @Test
  public void testUpdateDDO() throws Exception {
    given(verifyService.verifyIntegrityOfDDO(ddo)).willReturn(true);
    given(storageService.findDDObyDID(ddo.getId().toString())).willReturn(of(ddo));
    ddo.setUpdated(now());
    sendDDORequest(
        put(URL, ddo.getId().toString()).contentType(contentType)
            .content(json.write(ddo).getJson()),
        status().isOk());
  }

  @Test
  public void testDIDNotFoundOnUpdateDDO() throws Exception {
    given(verifyService.verifyIntegrityOfDDO(ddo)).willReturn(true);
    ddo.setUpdated(now());
    sendDDORequest(
        put(URL, ddo.getId().toString()).contentType(contentType)
            .content(json.write(ddo).getJson()),
        status().isNotFound());
  }

  @Test
  public void testCheckPublicKeyOnUpdateDDO() throws Exception {
    ddo.setPublicKey(null);
    ddo.setUpdated(now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testCheckProofOnUpdateDDO() throws Exception {
    ddo.setProof(null);
    ddo.setUpdated(now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testWrongUpdateDateOnUpdateDDO() throws Exception {
    ddo.setUpdated(ddo.getCreated());
    ddo.setCreated(now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isUnprocessableEntity());
  }

  @Test
  public void testInvalidProofExceptionOnCreateDDO() throws Exception {

    when(verifyService
        .isCreatorInPublicKeys(any(), any()))
        .thenReturn(false);
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  /* CREATING DDO */
  @Test
  public void testCreateDDO() throws Exception {
    given(verifyService.verifyIntegrityOfDDO(ddo)).willReturn(true);
    postRequest(status().isOk());
  }

  @Test
  public void testCreateLongDDO() throws Exception {
    ddo.setId(parse(DEFAULT_LONG_DID));
    postRequest(status().isUnprocessableEntity());
  }

  // TODO: 12/09/2018 implement tests for creating DDO using testRestTemplate

  @Test
  public void testDuplicateDDO() throws Exception {
    given(verifyService.verifyIntegrityOfDDO(ddo)).willReturn(true);
    given(storageService.findDDObyDID(ddo.getId().toString())).willReturn(of(ddo));
    postRequest(status().isOk());
  }

  @Test(expected = ParserException.class)
  public void testCheckDIDOnCreateDDO() throws Exception {
    ddo.setId(parse("sdg"));
    postRequest(status().isBadRequest());
  }

  @Test
  public void testCheckPublicKeyOnCreateDDO() throws Exception {
    ddo.setPublicKey(null);
    postRequest(status().isBadRequest());
  }

  @Test
  public void testCheckProofOnCreateDDO() throws Exception {
    ddo.setProof(null);
    postRequest(status().isBadRequest());
  }

  @Test
  public void testGetPublicKeysFromAnotherDDO() throws Exception {
    given(verifyService.verifyIntegrityOfDDO(ddo)).willReturn(true);
    ddo.setId(randomUUID());
    DID proofCreator = ddo.getProof().getOptions().getCreator();
    given(storageService.findDDObyDID(proofCreator.toString())).willReturn(of(ddo));
    postRequest(status().isOk());
  }

  private void postRequest(ResultMatcher expectedStatus) throws Exception {
    mvc.perform(post(PATH).contentType(contentType)
        .content(json.write(ddo).getJson())).andExpect(expectedStatus);
  }

  private void sendDDORequest(RequestBuilder builder, ResultMatcher... expectedResults)
      throws Exception {
    ResultActions resultActions = mvc.perform(builder);
    for (ResultMatcher expectedResult : expectedResults) {
      resultActions.andExpect(expectedResult);
    }
  }
}
