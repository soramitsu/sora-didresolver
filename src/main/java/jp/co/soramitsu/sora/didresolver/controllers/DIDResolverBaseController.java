package jp.co.soramitsu.sora.didresolver.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;
import jp.co.soramitsu.sora.didresolver.exceptions.BadProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.exceptions.InvalidProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.CryptoService;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DIDResolverBaseController.PATH)
@Api(value = DIDResolverBaseController.PATH, description = "CRUD operations on DID documents")
@Slf4j
public class DIDResolverBaseController {

  static final String PATH = "/did";
  static final String ID_PARAM = "/{did}";

  protected StorageService storageService;

  private CryptoService cryptoService;

  DIDResolverBaseController(StorageService storageService, CryptoService cryptoService) {
    this.storageService = storageService;
    this.cryptoService = cryptoService;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation("This operation is used to register new DID-DDO pair in Identity System")
  public void createDDO(
      @ApiParam(value = "url encoded DID", required = true) @Validated @RequestBody DDO ddo)
      throws UnparseableException {
    log.info("start execution of method createDDO for DID - {}", ddo.getId());
    verifyDDOProof(ddo);
    val optionalDDO = storageService.read(ddo.getId());
    if (optionalDDO.isPresent()) {
      throw new DIDDuplicateException(ddo.getId());
    }
    log.info("write to storage DDO with DID - {}", ddo.getId());
    storageService.createOrUpdate(ddo.getId(), ddo);
  }

  protected void verifyDDOProof(DDO ddo) {
    Proof proof = ddo.getProof().get(0);
    if (!cryptoService.checkProofCorrectness(proof, ddo.getId(), ddo.getPublicKey())) {
      throw new InvalidProofException(ddo.getId());
    }
    Optional<PublicKey> publicKey = cryptoService.getPublicKeyByProof(proof, ddo.getPublicKey());
    if (publicKey.isPresent() && !cryptoService
        .verifyDDOProof(ddo, publicKey.get().getPublicKeyValue())) {
      log.debug("failure verify proof for DDO with DID {}", ddo.getId());
      throw new BadProofException();
    }
    log.debug("success verify proof for DDO with DID {}", ddo.getId());
  }
}
