package jp.co.soramitsu.sora.didresolver.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    public void createDDO(@ApiParam(value = "url encoded DID", required = true) @Validated @RequestBody DDO ddo)
        throws UnparseableException {
        val optionalDDO = storageService.read(ddo.getId());
        if (optionalDDO.isPresent()) {
            throw new DIDDuplicateException(ddo.getId());
        }
        storageService.createOrUpdate(ddo.getId(), ddo);
    }

}