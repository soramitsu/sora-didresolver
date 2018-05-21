package jp.co.soramitsu.sora.didresolver.Controllers;

import jp.co.soramitsu.sora.didresolver.DAO.DDO;
import jp.co.soramitsu.sora.didresolver.Services.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rogachevsn
 */
@RestController
@RequestMapping(DIDResolverBaseController.PATH)
public class DIDResolverBaseController {

    public static final String PATH = "/did";

    /*protected final ValidateService validateService;

    @Autowired
    public DIDResolverBaseController(ValidateService validateService) {
        this.validateService = validateService;
    }*/

    @PostMapping
    public void registerDID(@RequestBody DDO ddo){
        System.out.println("registerDID");
    }
}
