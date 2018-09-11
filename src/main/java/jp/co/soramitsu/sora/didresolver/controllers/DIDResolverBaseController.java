package jp.co.soramitsu.sora.didresolver.controllers;

import static org.springframework.http.MediaType.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.didresolver.exceptions.BadProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import jp.co.soramitsu.sora.didresolver.exceptions.InvalidProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.ValidateService;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
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

  private VerifyService verifyService;

  private ValidateService validateService;

  protected DIDResolverBaseController(
      StorageService storageService, VerifyService verifyService, ValidateService validateService) {
    this.storageService = storageService;
    this.verifyService = verifyService;
    this.validateService = validateService;
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

  protected void verifyDDOProof(DDO ddo) throws UnparseableException {
//    todo: change to correct validation process
//    DID proofCreator = ddo.getProof().getOptions().getCreator();
//    if (!validateService.isProofCreatorInAuth(proofCreator, ddo.getAuthentication())
//        || !validateService.isProofInPublicKeys(
//            proofCreator, getPublicKeysForCheck(proofCreator, ddo.getId(), ddo.getPublicKey()))) {
//      throw new InvalidProofException(ddo.getId().toString());
//    }
//    Optional<PublicKey> publicKey =
//        verifyService.getProofPublicKeyByProof(ddo.getPublicKey(), ddo.getProof());
//    if (publicKey.isPresent()
//        && !verifyService.verifyDDOProof(ddo, publicKey.get().getId().toString())) {
//      log.warn("failure verify proof for DDO with DID {}", ddo.getId());
//      throw new BadProofException(ddo.getId().toString());
//    }
//    log.debug("success verify proof for DDO with DID {}", ddo.getId());
  }

  /**
   * Receives an array of proof's public keys for check proof's integrity
   *
   * @param proofCreator DID part of the value of field creator of proof section
   * @param subjectDid subject DID
   * @param subjectPublicKeys array of public keys of the current DDO
   * @return an array of Subject's public keys when Proof Creator and Subject DID are the same,
   *     otherwise return an array of Proof's public keys
   */
  private List<PublicKey> getPublicKeysForCheck(
      @NotBlank DID proofCreator,
      @NotBlank DID subjectDid,
      @NotNull @Valid List<PublicKey> subjectPublicKeys)
      throws UnparseableException {
    if (!subjectDid.equals(proofCreator)) {
      val proofCreatorDDO = storageService.findDDObyDID(proofCreator.toString());
      if (proofCreatorDDO.isPresent()) {
        return proofCreatorDDO.get().getPublicKey();
      }
    }
    return subjectPublicKeys;
  }
}
