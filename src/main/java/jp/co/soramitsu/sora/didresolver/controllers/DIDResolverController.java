package jp.co.soramitsu.sora.didresolver.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import lombok.val;
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
    public ResponseEntity<DDO> getDDO(@ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did)
        throws UnparseableException {
        val ddo = storageService.read(did).orElseThrow(() -> new DIDNotFoundException(did));
        return new ResponseEntity<>(ddo, HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value = "This operation is used for DDO revocation or removal.")
    public void deleteDDO(@ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did)
        throws UnparseableException {
        storageService.read(did).orElseThrow(() -> new DIDNotFoundException(did));
        storageService.delete(did);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "This operation essentially is a “Replace” operation, e.g. old DDO is replaced with new DDO given DID.")
    public void updateDDO(@ApiParam(value = "url encoded DID", required = true) @DIDConstraint(isNullable = false) @PathVariable String did,
                          @ApiParam(value = "New DDO MUST contain updated property with time > created", required = true) @Validated @RequestBody DDO ddo)
        throws UnparseableException {
        storageService.read(did).orElseThrow(() -> new DIDNotFoundException(did));
        storageService.createOrUpdate(did, ddo);
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
