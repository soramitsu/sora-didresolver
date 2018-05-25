package jp.co.soramitsu.sora.didresolver.controllers;

import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH + DIDResolverBaseController.ID_PARAM)
public class DIDResolverController extends DIDResolverBaseController{

    DIDResolverController(StorageService storageService){
        super(storageService);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DDO> getDDO(@PathVariable String did){
        checkDIDNotFound(did);
        return new ResponseEntity<>(storageService.read(did), HttpStatus.OK);
    }

    @DeleteMapping
    public void deleteDDO(@PathVariable String did){
        checkDIDNotFound(did);
        storageService.delete(did);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void updateDDO(@PathVariable String did, @Validated @RequestBody DDO ddo){
        checkDIDNotFound(did);
        storageService.createOrUpdate(did,ddo);
    }

    private void checkDIDNotFound(String did){
        if(!checkDDOByDidAtStorage(did)){
            throw new DIDNotFoundException(did);
        }
    }

}
