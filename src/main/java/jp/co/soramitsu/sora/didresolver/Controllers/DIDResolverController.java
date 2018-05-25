package jp.co.soramitsu.sora.didresolver.controllers;

import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDNotFoundException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        return new ResponseEntity<>(findDDOByDid(did), HttpStatus.OK);
    }

    @DeleteMapping
    public void deleteDDO(@PathVariable String did){
        findDDOByDid(did);
        storageService.delete(did);
    }

    private DDO findDDOByDid(String did){
        DDO ddo = storageService.read(did);
        if (ddo == null){
            throw new DIDNotFoundException(did);
        }
        return ddo;
    }
}
