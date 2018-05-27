package jp.co.soramitsu.sora.didresolver.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH)
@Api(value = DIDResolverBaseController.PATH, description = "CRUD operations on DID documents")
public class DIDResolverBaseController {

    static final String PATH = "/did";
    static final String ID_PARAM = "/{did}";

    StorageService storageService;

    DIDResolverBaseController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation("This operation is used to register new DID-DDO pair in Identity System")
    public void createDDO(@ApiParam(value = "url encoded DID", required = true) @Validated @RequestBody DDO ddo) {
        if (checkDDOByDidAtStorage(ddo.getId())) {
            throw new DIDDuplicateException(ddo.getId());
        }
        storageService.createOrUpdate(ddo.getId(), ddo);
    }

    boolean checkDDOByDidAtStorage(String did) {
        return Optional.ofNullable(storageService.read(did)).isPresent();
    }

}
