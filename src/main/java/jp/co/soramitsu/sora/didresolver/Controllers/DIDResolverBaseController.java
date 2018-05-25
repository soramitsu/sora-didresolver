package jp.co.soramitsu.sora.didresolver.controllers;

import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH)
public class DIDResolverBaseController {

    public static final String PATH = "/did";
    public static final String ID_PARAM = "/{did}";

    protected StorageService storageService;

    DIDResolverBaseController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PutMapping(path = DIDResolverBaseController.ID_PARAM,consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void createOrUpdateDDO(@Validated @RequestBody DDO ddo) {
        if (storageService.read(ddo.getId()) != null){
            throw new DIDDuplicateException(ddo.getId());
        }
        storageService.createOrUpdate(ddo.getId(), ddo);
    }
}
