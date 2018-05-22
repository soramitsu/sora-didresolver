package jp.co.soramitsu.sora.didresolver.Controllers;

import jp.co.soramitsu.sora.didresolver.DTO.DDO;
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

    /*protected final ValidateService validateService;

    @Autowired
    public DIDResolverBaseController(ValidateService validateService) {
        this.validateService = validateService;
    }*/

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void createDDO(@Validated @RequestBody DDO ddo){

    }
}
