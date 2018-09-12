package jp.co.soramitsu.sora.didresolver.controllers;

import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.ID_PARAM;
import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.PATH;
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

import java.time.Instant;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
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

  private static final String INCORRECT_DID = "did:sora:iroha:5LkqENiDNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn";

  @Test
  public void testGetDDO() throws Exception {
    given(storageService.findDDObyDID(ddo.getId().toString())).willReturn(Optional.of(ddo));
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
    ddo.setId(DID.parse(INCORRECT_DID));
    sendDDORequest(get(URL, ddo.getId()), status().isBadRequest());
  }

  @Test
  public void testDeleteDDO() throws Exception {
    given(storageService.findDDObyDID(ddo.getId().toString())).willReturn(Optional.of(ddo));
    sendDDORequest(delete(URL, ddo.getId()), status().isOk());
  }

  @Test
  public void testDIDNotFoundOnDeleteDDO() throws Exception {
    doThrow(new DIDNotFoundException(ddo.getId().toString())).when(storageService).delete(ddo.getId().toString());
    sendDDORequest(delete(URL, ddo.getId()), status().isNotFound());
  }

  @Test
  public void testIncorrectDIDOnDeleteDDO() throws Exception {
    // set incorrect did for check validation
    ddo.setId(DID.parse(INCORRECT_DID));
    sendDDORequest(delete(URL, ddo.getId()), status().isBadRequest());
  }

  @Test
  public void testUpdateDDO() throws Exception {
    given(storageService.findDDObyDID(ddo.getId().toString())).willReturn(Optional.of(ddo));
    ddo.setUpdated(Instant.now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isOk());
  }

  @Test
  public void testDIDNotFoundOnUpdateDDO() throws Exception {
    ddo.setUpdated(Instant.now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isNotFound());
  }

  @Test
  public void testIncorrectDIDOnUpdateDDO() throws Exception {
    // set incorrect did for check validation
    ddo.setId(DID.parse(INCORRECT_DID));
    ddo.setUpdated(Instant.now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testCheckPublicKeyOnUpdateDDO() throws Exception {
    ddo.setPublicKey(null);
    ddo.setUpdated(Instant.now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testCheckAuthenticationOnUpdateDDO() throws Exception {
    ddo.setAuthentication(null);
    ddo.setUpdated(Instant.now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testCheckProofOnUpdateDDO() throws Exception {
    ddo.setProof(null);
    ddo.setUpdated(Instant.now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testWrongUpdateDateOnUpdateDDO() throws Exception {
    ddo.setUpdated(ddo.getCreated());
    ddo.setCreated(Instant.now());
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testInvalidProofExceptionOnCreateDDO() throws Exception {
    when(validateService
        .isProofCreatorInAuth(ddo.getProof().getOptions().getCreator(), ddo.getAuthentication()))
        .thenReturn(false);
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());

    when(validateService
        .isProofInPublicKeys(any(), any()))
        .thenReturn(false);
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());

    when(validateService
        .isProofCreatorInAuth(ddo.getProof().getOptions().getCreator(), ddo.getAuthentication()))
        .thenReturn(true);
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isBadRequest());
  }

  @Test
  public void testBadProofExceptionOnCreateDDO() throws Exception {
    when(verifyService.verifyIntegrityOfDDO(any(), any())).thenReturn(false);
    sendDDORequest(
        put(URL, ddo.getId()).contentType(contentType).content(json.write(ddo).getJson()),
        status().isUnauthorized());
  }

  @Test
  public void testCreateDDO() throws Exception {
    postRequest(status().isOk());
  }

  @Test
  public void testDuplicateDDO() throws Exception {
    given(storageService.findDDObyDID(ddo.getId().toString())).willReturn(Optional.of(ddo));
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
        .isProofCreatorInAuth(ddo.getProof().getOptions().getCreator(), ddo.getAuthentication()))
        .thenReturn(false);
    postRequest(status().isBadRequest());

    when(validateService
        .isProofInPublicKeys(any(), any()))
        .thenReturn(false);
    postRequest(status().isBadRequest());

    when(validateService
        .isProofCreatorInAuth(ddo.getProof().getOptions().getCreator(), ddo.getAuthentication()))
        .thenReturn(true);
    postRequest(status().isBadRequest());
  }

  @Test
  public void testBadProofExceptionOnCreateDDO() throws Exception {
    when(verifyService.verifyIntegrityOfDDO(any(), any())).thenReturn(false);
    postRequest(status().isUnauthorized());
  }

  @Test
  public void testGetPublicKeysFromAnotherDDO() throws Exception {
    ddo.setId(DID.randomUUID());
    DID proofCreator = ddo.getProof().getOptions().getCreator();
    given(storageService.findDDObyDID(proofCreator.toString())).willReturn(Optional.of(ddo));
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
