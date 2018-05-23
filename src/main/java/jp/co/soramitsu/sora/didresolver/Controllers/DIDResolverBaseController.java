package jp.co.soramitsu.sora.didresolver.Controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = DIDResolverBaseController.PATH, description = "CRUD operations on DID documents")
public class DIDResolverBaseController {

    public static final String PATH = "/did";

    /*protected final ValidateService validateService;

    @Autowired
    public DIDResolverBaseController(ValidateService validateService) {
        this.validateService = validateService;
    }*/

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation("This operation is used to register new DID-DDO pair in Identity System")
    public void createDDO(@ApiParam(value = "url encoded DID", required = true) @Validated @RequestBody DDO ddo){

    }
}
