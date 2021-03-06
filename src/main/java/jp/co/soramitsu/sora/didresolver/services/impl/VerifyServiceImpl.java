package jp.co.soramitsu.sora.didresolver.services.impl;

import static java.util.Objects.nonNull;
import static jp.co.soramitsu.crypto.ed25519.spec.EdDSANamedCurveTable.ED_25519;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.crypto.ed25519.EdDSAPublicKey;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSANamedCurveTable;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAParameterSpec;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAPublicKeySpec;
import jp.co.soramitsu.sora.didresolver.exceptions.ProofSignatureVerificationException;
import jp.co.soramitsu.sora.didresolver.exceptions.PublicKeyValueNotPresentedException;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.sdk.crypto.common.SecurityProvider;
import jp.co.soramitsu.sora.sdk.crypto.json.JSONCanonizerWithOneCoding;
import jp.co.soramitsu.sora.sdk.crypto.json.JSONEd25519Sha3SignatureSuite;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
import jp.co.soramitsu.sora.sdk.json.JsonUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
@Slf4j
public class VerifyServiceImpl implements VerifyService {

  private static final ObjectMapper mapper = JsonUtil.buildMapper();

  private static final EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName(ED_25519);
  private static final SecurityProvider secProvider = new SecurityProvider();
  private static final JSONCanonizerWithOneCoding canonizer = new JSONCanonizerWithOneCoding();

  @Override
  public boolean isCreatorInPublicKeys(@NotNull DID proofCreator, List<PublicKey> publicKeys) {
    return nonNull(publicKeys) && publicKeys.stream()
        .anyMatch(key -> proofCreator.equals(key.getId()));
  }

  @Override
  public boolean isCreatorInAuth(
      @NotNull DID creator, @NotNull @Valid List<Authentication> authentication) {

    return authentication
        .stream()
        .anyMatch(auth -> auth.getPublicKey().toString().equals(creator.toString()));
  }

  @Override
  public boolean verifyIntegrityOfDDO(DDO ddo, JsonNode jsonDDO)
      throws ProofSignatureVerificationException, PublicKeyValueNotPresentedException {
    log.debug("verifying integrity of DDO with DID {}", ddo.getId());

    byte[] publicKeyValue =
        getPublicKeyValueByDID(ddo.getPublicKey(), ddo.getProof().getOptions().getCreator())
            .orElseThrow(
                () ->
                    new PublicKeyValueNotPresentedException(
                        ddo.getProof().getOptions().getCreator().toString()));

    EdDSAPublicKey edDSAPublicKey =
        new EdDSAPublicKey(new EdDSAPublicKeySpec(publicKeyValue, parameterSpec));

    boolean isDDOVerified;
    try {
      val suite = new JSONEd25519Sha3SignatureSuite(secProvider, canonizer, mapper);
      isDDOVerified = suite.verify(jsonDDO, edDSAPublicKey);
    } catch (Exception e) {
      throw new ProofSignatureVerificationException(ddo.getId().toString(), e);
    }
    log.debug("finishing verification of proof for DDO with DID {}", ddo.getId());
    return isDDOVerified;
  }

  /**
   * Receives the public key from a given collection of public keys that matches DID's id
   *
   * @param publicKeys collection of public keys of document
   * @param did of the owner of the receiving Public Key
   * @return public key in bytes
   */
  private Optional<byte[]> getPublicKeyValueByDID(List<PublicKey> publicKeys, DID did) {
    log.trace("get public key for did {}", did);
    return publicKeys
        .stream()
        .filter(key -> key.getId().toString().equals(did.toString()))
        .findFirst()
        .map(PublicKey::getPublicKey);
  }
}
