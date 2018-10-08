package jp.co.soramitsu.sora.didresolver.controllers;

import static java.util.Objects.isNull;
import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.ID_PARAM;
import static jp.co.soramitsu.sora.didresolver.commons.URIConstants.PATH;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.ResponseEntity.notFound;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.exceptions.IncorrectUpdateException;
import jp.co.soramitsu.sora.didresolver.exceptions.InvalidProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.ProofSignatureVerificationException;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(PATH)
@Api(value = PATH, description = "CRUD operations on DID documents")
@Validated
@Slf4j
public class DIDResolverController {

  private StorageService storageService;
  private VerifyService verifyService;

  @PostMapping(consumes = {APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation("This operation is used to register new DID-DDO pair in Identity System")
  public ResponseEntity createDDO(
      @ApiParam(value = "url encoded DID", required = true) @Validated @RequestBody DDO ddo)
      throws UnparseableException {
    log.info("starting creation of DDO for DID - {}", ddo.getId());
    verifyDDOProof(ddo);
    val optionalDDO = storageService.findDDObyDID(ddo.getId().toString());
    if (optionalDDO.isPresent()) {
      throw new DIDDuplicateException(ddo.getId().toString());
    }
    log.info("write to storage DDO with DID - {}", ddo.getId());
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
    return new ResponseEntity(OK);
  }

  @GetMapping(value = ID_PARAM, produces = {APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation(value = "This operation is used to query DDO given DID.", response = ResponseEntity.class)
  public ResponseEntity<DDO> getDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did)
      throws UnparseableException {
    log.info("Receive DDO by DID - {}", did);
    val ddo = storageService.findDDObyDID(did);
    return ddo.map(ResponseEntity::ok).orElseGet(() -> notFound().build());
  }

  @DeleteMapping(value = ID_PARAM)
  @ApiOperation(value = "This operation is used for DDO revocation or removal.")
  public void deleteDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did)
      throws UnparseableException {
    log.info("Delete DDO by DID - {}", did);
    storageService.delete(did);
  }

  @PutMapping(value = ID_PARAM, consumes = {APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation(value = "This operation essentially is a “Replace” operation, e.g. old DDO is replaced with new DDO given DID.")
  public void updateDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did,
      @ApiParam(value = "New DDO MUST contain updated property with time > created", required = true) @Validated @RequestBody DDO ddo)
      throws UnparseableException {
    log.info("Update DDO by DID - {}", did);
    verifyDDOProof(ddo);
    if (!ddo.getUpdated().isAfter(ddo.getCreated())) {
      throw new IncorrectUpdateException(ddo.getCreated().toString(), ddo.getUpdated().toString());
    }
    storageService.findDDObyDID(did).orElseThrow(() -> new DIDNotFoundException(did));
    storageService.createOrUpdate(did, ddo);
  }

  private void verifyDDOProof(DDO ddo) {
    checkCreatorValidity(ddo);

    if (!verifyService.verifyIntegrityOfDDO(ddo)) {
      throw new ProofSignatureVerificationException(ddo.getId().toString());
    }

    log.debug("proof has been successfully verified for DDO with DID {}", ddo.getId());
  }

  private void checkCreatorValidity(DDO ddo) {
    if (isNull(ddo.getProof())) {
      throw new InvalidProofException(ddo.getId().toString());
    }
    DID proofCreator = ddo.getProof().getOptions().getCreator();
    if (!verifyService.isCreatorInPublicKeys(proofCreator, ddo.getPublicKey())) {
      throw new InvalidProofException(ddo.getId().toString());
    }
  }

}
