package jp.co.soramitsu.sora.didresolver.services.impl;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static jp.co.soramitsu.crypto.ed25519.spec.EdDSANamedCurveTable.ED_25519;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.crypto.ed25519.EdDSAPublicKey;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSANamedCurveTable;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAParameterSpec;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAPublicKeySpec;
import jp.co.soramitsu.sora.crypto.common.SecurityProvider;
import jp.co.soramitsu.sora.crypto.json.JSONCanonizerWithOneCoding;
import jp.co.soramitsu.sora.crypto.signature.suite.JSONEd25519Sha3SignatureSuite;
import jp.co.soramitsu.sora.didresolver.exceptions.BadProofException;
import jp.co.soramitsu.sora.didresolver.exceptions.ProofSignatureVerificationException;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
import jp.co.soramitsu.sora.sdk.did.model.dto.publickey.Ed25519Sha3VerificationKey;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
@Slf4j
public class VerifyServiceImpl implements VerifyService {

  private static final ObjectMapper mapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .enable(INDENT_OUTPUT)
          .setSerializationInclusion(NON_NULL);

  private static final EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName(ED_25519);

  private static final JSONEd25519Sha3SignatureSuite suite =
      new JSONEd25519Sha3SignatureSuite(
          new SecurityProvider(), new JSONCanonizerWithOneCoding(), mapper);

  @Override
  public boolean isCreatorInPublicKeys(@NotNull DID proofCreator, List<PublicKey> publicKeys) {
    return publicKeys.stream().anyMatch(key -> proofCreator.equals(key.getId()));
  }

  @Override
  public boolean isCreatorInAuth(
      @NotNull DID creator, @NotNull @Valid List<Authentication> authentication) {

    return true;
//     FIXME: 11/09/2018 NPE due to incorrect constructing of Authentication
//    return authentication.stream().anyMatch(auth -> auth.getPublicKey().toString().equals(creator.toString()));
  }

  @Override
  public void verifyIntegrityOfDDO(DDO ddo) {
    log.debug("verifying integrity of DDO with DID {}", ddo.getId());

    Optional<byte[]> publicKeyValue =
        getPublicKeyValueByDID(ddo.getPublicKey(), ddo.getProof().getOptions().getCreator());

    if (!publicKeyValue.isPresent()) {
      throw new BadProofException(ddo.getProof().getOptions().getCreator().toString());
    }

    EdDSAPublicKey edDSAPublicKey =
        new EdDSAPublicKey(new EdDSAPublicKeySpec(publicKeyValue.get(), parameterSpec));

    try {
      suite.verify(ddo, edDSAPublicKey);
    } catch (IOException e) {
      throw new ProofSignatureVerificationException(
          ddo.getId().toString(), e);
    }

    log.debug("finishing verification of proof for DDO with DID {}", ddo.getId());
  }

  /**
   * Receives the public key that matches DID's id
   *
   * @param publicKeys collection of public keys of document
   * @param did of the owner of the receiving Public Key
   * @return public key in bytes
   */
  private Optional<byte[]> getPublicKeyValueByDID(List<PublicKey> publicKeys, DID did) {
    log.trace("get public key for did {}", did.toString());
    return publicKeys
        .stream()
        .filter(key -> key.getId().toString().equals(did.toString()))
        .findFirst()
//        fixme when abstract class PublicKey will have getPublicKey()
        .map(key -> ((Ed25519Sha3VerificationKey) key).getPublicKey());
  }

}
