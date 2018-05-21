package jp.co.soramitsu.sora.didresolver.Controllers;

import org.springframework.web.bind.annotation.*;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH+"/{did}")
public class DIDResolverController extends DIDResolverBaseController{

    @GetMapping
    public void getDDO(@PathVariable String did){

    }

    @PutMapping
    public void updateDDO(@PathVariable String did){

    }

    @DeleteMapping
    public void deleteDDO(@PathVariable String did){

    }
}
