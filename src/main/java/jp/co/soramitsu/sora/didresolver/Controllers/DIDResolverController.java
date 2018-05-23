package jp.co.soramitsu.sora.didresolver.Controllers;

import jp.co.soramitsu.sora.didresolver.DTO.DDO;
import jp.co.soramitsu.sora.didresolver.Services.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH+"/{did}")

public class DIDResolverController extends DIDResolverBaseController{

    DIDResolverController(StorageService storageService){
        super(storageService);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DDO> getDDO(@PathVariable String did){
        return new ResponseEntity<DDO>(storageService.read(did), HttpStatus.OK);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void updateDDO(@PathVariable String did, @Validated @RequestBody DDO ddo){
        storageService.createOrUpdate(did, ddo);
    }

    @DeleteMapping
    public void deleteDDO(@PathVariable String did){
        storageService.delete(did);
    }
}
