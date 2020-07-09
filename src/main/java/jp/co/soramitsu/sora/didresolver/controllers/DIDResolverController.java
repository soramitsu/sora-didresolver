package jp.co.soramitsu.sora.didresolver.controllers;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Objects.isNull;
import static jp.co.soramitsu.sora.didresolver.commons.CommonsConst.MAX_IROHA_KEY_LENGTH;
import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.ID_PARAM;
import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;
import javax.validation.ValidationException;
import javax.validation.Validator;
import jp.co.soramitsu.sora.didresolver.controllers.dto.GenericResponse;
import jp.co.soramitsu.sora.didresolver.controllers.dto.GetDDORs;
import jp.co.soramitsu.sora.didresolver.controllers.dto.SuccessfulResponse;
import jp.co.soramitsu.sora.didresolver.exceptions.DDOUnparseableException;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDIsTooLongException;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.exceptions.IncorrectUpdateException;
import jp.co.soramitsu.sora.didresolver.exceptions.InvalidProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.ProofSignatureVerificationException;
import jp.co.soramitsu.sora.didresolver.exceptions.PublicKeyValueNotPresentedException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.json.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * S4529 - Exposing Spring endpoints - warning for security auditors to check if endpoint is safe
 */
@RestController
@AllArgsConstructor
@RequestMapping(PATH)
@Api(value = PATH)
@Slf4j
@SuppressWarnings("squid:S4529")
public class DIDResolverController {

  private StorageService storageService;
  private VerifyService verifyService;
  private Validator validator;

  private ObjectMapper mapper = JsonUtil.buildMapper();

  private static final Function<String, LocalDateTime> DATE_TIME_MAPPER = iso8601String ->
      LocalDateTime.parse(iso8601String, ISO_DATE_TIME);

  @PostMapping(consumes = {APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation("This operation is used to register new DID-DDO pair in Identity System")
  @ApiResponses({
      @ApiResponse(
          code = 200,
          message = "Server returns GenericResponse which can contain next statuses:\n"
              + "OK - Returns when DID-DDO pair successfully registered.\n"
              + "DID_IS_TOO_LONG - Returns when DID is longer than Iroha key max size\n"
              + "DID_DUPLICATE - Returns when DID has already registered\n"
              + "INVALID_PROOF_SIGNATURE - Returns when proof signature verification for DDO has failed\n"
              + "INVALID_PROOF - Returns when proof is invalid\n"
              + "PUBLIC_KEY_VALUE_NOT_PRESENTED - Returns when public key value has not found",
          response = GenericResponse.class
      ),
      @ApiResponse(
          code = 400,
          message = "Failed. Returns when validation of received DDO has failed")})
  public ResponseEntity<GenericResponse> createDDO(
      @ApiParam(value = "url encoded DID", required = true) @RequestBody String ddoJson)
      throws DIDIsTooLongException, DIDDuplicateException, ProofSignatureVerificationException, InvalidProofException, PublicKeyValueNotPresentedException, DDOUnparseableException {
    DDO ddo = deserialize(ddoJson);
    final String id = ddo.getId().toString();
    log.info("starting creation of DDO for DID - {}", id);
    if (id.length() > MAX_IROHA_KEY_LENGTH) {
      throw new DIDIsTooLongException(id);
    }
    verifyDDOProof(ddo, ddoJson);
    val optionalDDO = storageService.findDDObyDID(id);
    if (optionalDDO.isPresent()) {
      throw new DIDDuplicateException(id);
    }
    log.info("write to storage DDO with DID - {}", id);
    storageService.createOrUpdate(id, ddoJson);
    return ok(new SuccessfulResponse());
  }

  @GetMapping(value = ID_PARAM, produces = {APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation(value = "This operation is used to query DDO given DID.", response = ResponseEntity.class)
  @ApiResponses({
      @ApiResponse(
          code = 200,
          message = "Server returns GetDDORs which can contain next statuses:\n"
              + "OK - Returns when DID-DDO pair successfully registered.\n"
              + "DID_NOT_FOUND - Returns when DID has not found",
          response = GetDDORs.class)})
  public ResponseEntity<GetDDORs> getDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did)
      throws DIDNotFoundException, DDOUnparseableException {
    log.info("Receive DDO by DID - {}", did);
    val ddo = storageService.findDDObyDID(did).orElseThrow(() -> new DIDNotFoundException(did));
    return ok(new GetDDORs(ddo));
  }

  @DeleteMapping(value = ID_PARAM)
  @ApiOperation(value = "This operation is used for DDO revocation or removal.")
  public ResponseEntity<GenericResponse> deleteDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did) {
    log.info("Delete DDO by DID - {}", did);
    storageService.delete(did);
    return ok(new SuccessfulResponse());
  }

  @PutMapping(value = ID_PARAM, consumes = {APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation(value = "This operation essentially is a “Replace” operation, e.g. old DDO is replaced with new DDO given DID.")
  @ApiResponses({
      @ApiResponse(
          code = 200,
          message = "Server returns GenericResponse which can contain next statuses:\n"
              + "OK - Returns when DID-DDO pair successfully registered.\n"
              + "DID_NOT_FOUND - Returns when DID has not found\n"
              + "INVALID_PROOF_SIGNATURE - Returns when proof signature verification for DDO has failed\n"
              + "INVALID_PROOF - Returns when proof is invalid\n"
              + "INCORRECT_UPDATE_TIME - Returns when updated time less than created time or not set\n"
              + "PUBLIC_KEY_VALUE_NOT_PRESENTED - Returns when public key value has not found",
          response = GenericResponse.class
      ),
      @ApiResponse(
          code = 400,
          message = "Failed. Returns when validation of received DDO has failed")})
  public ResponseEntity<GenericResponse> updateDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did,
      @ApiParam(value = "New DDO MUST contain updated property with time > created", required = true) @RequestBody String ddoJson)
      throws IncorrectUpdateException, DIDNotFoundException, ProofSignatureVerificationException, InvalidProofException, PublicKeyValueNotPresentedException, DDOUnparseableException {
    log.info("Update DDO by DID - {}", did);
    DDO ddo = deserialize(ddoJson);
    verifyDDOProof(ddo, ddoJson);
    if (!checkUpdatedTimeAfterCreatedTime(ddo)) {
      throw new IncorrectUpdateException(ddo.getId(), ddo.getCreated(), ddo.getUpdated());
    }
    if (storageService.findDDObyDID(did).isPresent()) {
      storageService.createOrUpdate(did, ddoJson);
    } else {
      throw new DIDNotFoundException(did);
    }
    return ok(new SuccessfulResponse());
  }

  @SneakyThrows(IOException.class)
  private void verifyDDOProof(DDO ddo, String ddoJson)
      throws ProofSignatureVerificationException, InvalidProofException, PublicKeyValueNotPresentedException {
    checkCreatorValidity(ddo);

    if (!verifyService.verifyIntegrityOfDDO(ddo, mapper.readTree(ddoJson))) {
      throw new ProofSignatureVerificationException(ddo.getId().toString());
    }

    log.debug("proof has been successfully verified for DDO with DID {}", ddo.getId());
  }

  private void checkCreatorValidity(DDO ddo)
      throws InvalidProofException, PublicKeyValueNotPresentedException {
    if (isNull(ddo.getProof())) {
      throw new InvalidProofException(ddo.getId().toString());
    }
    DID proofCreator = ddo.getProof().getOptions().getCreator();
    if (!verifyService.isCreatorInPublicKeys(proofCreator, ddo.getPublicKey())) {
      throw new PublicKeyValueNotPresentedException(proofCreator.toString());
    }
  }

  private boolean checkUpdatedTimeAfterCreatedTime(DDO ddo) {
    return Optional.ofNullable(ddo.getUpdated())
        .map(DATE_TIME_MAPPER)
        .map(updated -> updated.isAfter(DATE_TIME_MAPPER.apply(ddo.getCreated())))
        .orElse(false);
  }

  public DDO deserialize(String json) throws DDOUnparseableException {
    try {
      val ddo = mapper.readValue(json, DDO.class);
      if (ddo == null) {
        throw new ValidationException("DDO is null");
      }
      val errors = validator.validate(ddo);
      if (!errors.isEmpty()) {
        throw new ValidationException("DDO violates constraints:" + errors);
      }
      return ddo;
    } catch (IOException | ValidationException e) {
      log.error("Could not handle DDO", e);
      throw new DDOUnparseableException(e);
    }
  }
}
