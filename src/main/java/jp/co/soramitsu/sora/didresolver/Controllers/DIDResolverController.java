package jp.co.soramitsu.sora.didresolver.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestController
@RequestMapping(DIDResolverBaseController.PATH + DIDResolverBaseController.ID_PARAM)
@Validated
public class DIDResolverController extends DIDResolverBaseController {

    private static final String ERROR_FORMAT = "{ \"error\" : \"%s\"}\n";

    DIDResolverController(StorageService storageService) {
        super(storageService);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "This operation is used to query DDO given DID.", response = ResponseEntity.class)
    public ResponseEntity<DDO> getDDO(@ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did) {
        checkDIDNotFound(did);
        return new ResponseEntity<>(storageService.read(did), HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value = "This operation is used for DDO revocation or removal.")
    public void deleteDDO(@ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did) {
        checkDIDNotFound(did);
        storageService.delete(did);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "This operation essentially is a “Replace” operation, e.g. old DDO is replaced with new DDO given DID.")
    public void updateDDO(@ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did,
                          @ApiParam(value = "New DDO MUST contain updated property with time > created", required = true) @Validated @RequestBody DDO ddo) {
        checkDIDNotFound(did);
        storageService.createOrUpdate(did, ddo);
    }

    /**
     * check that the DID already exists in the storage
     *
     * @param did - valid URLEncoded DID
     * @throws DIDNotFoundException when did is not registered
     */
    private void checkDIDNotFound(String did) {
        if (!checkDDOByDidAtStorage(did)) {
            throw new DIDNotFoundException(did);
        }
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            strBuilder.append(String.format(ERROR_FORMAT,violation.getMessage()));
        }
        return new ResponseEntity<>(strBuilder.toString(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
