package jp.co.soramitsu.sora.didresolver.Controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jp.co.soramitsu.sora.didresolver.DTO.DDO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH + "/{did}")
public class DIDResolverController extends DIDResolverBaseController {

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "This operation is used to query DDO given DID.", response = ResponseEntity.class)
    public ResponseEntity<DDO> getDDO(@ApiParam(value = "url encoded DID", required = true) @PathVariable String did) {

        return new ResponseEntity<DDO>(new DDO(), HttpStatus.OK);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "This operation essentially is a “Replace” operation, e.g. old DDO is replaced with new DDO given DID.")
    public void updateDDO(@ApiParam(value = "url encoded DID", required = true) @PathVariable String did,
                          @ApiParam(value = "New DDO MUST contain updated property with time > created", required = true) @Validated @RequestBody DDO ddo) {

    }

    @DeleteMapping
    @ApiOperation(value = "This operation is used for DDO revocation or removal.")
    public void deleteDDO(@ApiParam(value = "url encoded DID", required = true) @PathVariable String did) {

    }
}
