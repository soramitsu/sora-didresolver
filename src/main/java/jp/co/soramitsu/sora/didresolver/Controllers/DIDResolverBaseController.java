package jp.co.soramitsu.sora.didresolver.controllers;

import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH)
public class DIDResolverBaseController {

    static final String PATH = "/did";
    static final String ID_PARAM = "/{did}";

    StorageService storageService;

    DIDResolverBaseController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void createDDO(@Validated @RequestBody DDO ddo) {
        if (checkDDOByDidAtStorage(ddo.getId())){
            throw new DIDDuplicateException(ddo.getId());
        }
        storageService.createOrUpdate(ddo.getId(), ddo);
    }

    boolean checkDDOByDidAtStorage(String did) {
        return Optional.ofNullable(storageService.read(did)).isPresent();
    }
}
