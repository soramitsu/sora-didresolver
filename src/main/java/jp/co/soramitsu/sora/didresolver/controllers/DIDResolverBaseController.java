package jp.co.soramitsu.sora.didresolver.controllers;

import static org.springframework.http.MediaType.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.exceptions.BadProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.exceptions.InvalidProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
import jp.co.soramitsu.sora.sdk.did.model.dto.publickey.Ed25519Sha3VerificationKey;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

  private VerifyService verifyService;

  protected DIDResolverBaseController(
      StorageService storageService, VerifyService verifyService) {
    this.storageService = storageService;
    this.verifyService = verifyService;
  }

  @PostMapping(consumes = {APPLICATION_JSON_UTF8_VALUE})
  @ApiOperation("This operation is used to register new DID-DDO pair in Identity System")
  public void createDDO(
      @ApiParam(value = "url encoded DID", required = true) @Validated @RequestBody DDO ddo)
      throws UnparseableException {
    log.info("start execution of method createDDO for DID - {}", ddo.getId());
    verifyDDOProof(ddo);
    val optionalDDO = storageService.findDDObyDID(ddo.getId().toString());
    if (optionalDDO.isPresent()) {
      throw new DIDDuplicateException(ddo.getId().toString());
    }
    log.info("write to storage DDO with DID - {}", ddo.getId());
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
  }

  protected void verifyDDOProof(DDO ddo) {
    DID proofCreator = ddo.getProof().getOptions().getCreator();
    if (!verifyService.isProofCreatorInAuth(proofCreator, ddo.getAuthentication())
        || !verifyService.isProofInPublicKeys(
            proofCreator, ddo.getPublicKey())) {
      throw new InvalidProofException(ddo.getId().toString());
    }

    Optional<byte[]> publicKey =
        getPublicKeyValueByDID(ddo.getPublicKey(), ddo.getProof().getOptions().getCreator());
    if (publicKey.isPresent()
        && !verifyService.verifyIntegrityOfDDO(ddo, publicKey.get())) {
      log.warn("failure verify proof for DDO with DID {}", ddo.getId());
      throw new BadProofException(ddo.getId().toString());
    }
    log.debug("success verify proof for DDO with DID {}", ddo.getId());
  }

  /**
   * Receives the public key that matches DID's id
   *
   * @param publicKeys collection of public keys of document
   * @param did of the owner of the receiving Public Key
   * @return public key in bytes
   */
  protected Optional<byte[]> getPublicKeyValueByDID(List<PublicKey> publicKeys, DID did) {
    log.debug("get public key for proof {}, {}, {}", did.toString());
    return publicKeys
        .stream()
        .filter(key -> key.getId().toString().equals(did.toString()))
        .findFirst()
        .map(key -> ((Ed25519Sha3VerificationKey) key).getPublicKey());
  }
}
