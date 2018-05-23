package jp.co.soramitsu.sora.didresolver.Controllers;

import jp.co.soramitsu.sora.didresolver.DTO.DDO;
import jp.co.soramitsu.sora.didresolver.Services.StorageService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH)
public class DIDResolverBaseController {

    public static final String PATH = "/did";

    protected StorageService storageService;

    DIDResolverBaseController(StorageService storageService){
        this.storageService = storageService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void createDDO(@Validated @RequestBody DDO ddo){
        storageService.createOrUpdate(ddo.getId(),ddo);
    }
}
