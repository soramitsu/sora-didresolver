package jp.co.soramitsu.sora.didresolver.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DIDResolverBaseController.PATH + DIDResolverBaseController.ID_PARAM)
@Validated
@Slf4j
public class DIDResolverController extends DIDResolverBaseController {

  private static final String ERROR_FORMAT = "{ \"error\" : \"%s\"}\n";

  DIDResolverController(StorageService storageService) {
    super(storageService);
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation(value = "This operation is used to query DDO given DID.", response = ResponseEntity.class)
  public ResponseEntity<DDO> getDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did)
      throws UnparseableException {
    log.info("Recieve DDO by DID - " + did);
    val ddo = storageService.read(did).orElseThrow(() -> new DIDNotFoundException(did));
    return new ResponseEntity<>(ddo, HttpStatus.OK);
  }

  @DeleteMapping
  @ApiOperation(value = "This operation is used for DDO revocation or removal.")
  public void deleteDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did)
      throws UnparseableException {
    log.info("Delete DDO by DID - " + did);
    storageService.read(did).orElseThrow(() -> new DIDNotFoundException(did));
    storageService.delete(did);
  }

  @PutMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation(value = "This operation essentially is a “Replace” operation, e.g. old DDO is replaced with new DDO given DID.")
  public void updateDDO(
      @ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did,
      @ApiParam(value = "New DDO MUST contain updated property with time > created", required = true) @Validated @RequestBody DDO ddo)
      throws UnparseableException {
    log.info("Update DDO by DID - " + did);
    storageService.read(did).orElseThrow(() -> new DIDNotFoundException(did));
    storageService.createOrUpdate(did, ddo);
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    StringBuilder strBuilder = new StringBuilder();
    for (ConstraintViolation<?> violation : violations) {
      strBuilder.append(String.format(ERROR_FORMAT, violation.getMessage()));
    }
    log.error(e.toString(), e);
    return new ResponseEntity<>(strBuilder.toString(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }
}
