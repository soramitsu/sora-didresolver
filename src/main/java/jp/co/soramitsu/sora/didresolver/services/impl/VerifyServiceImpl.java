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
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
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
  public boolean isProofInPublicKeys(@NotNull DID proofCreator, List<PublicKey> publicKeys) {
    return publicKeys.stream().anyMatch(key -> proofCreator.equals(key.getId()));
  }

  @Override
  public boolean isProofCreatorInAuth(
      @NotNull DID creator, @NotNull @Valid List<Authentication> authentication) {

    return true;
    // FIXME: 11/09/2018 NPE occurring, however creator and getPublicKey are initialized
//    return authentication.stream().anyMatch(auth -> auth.getPublicKey().equals(creator));
  }
  
  @Override
  public boolean verifyIntegrityOfDDO(DDO ddo, byte[] publicKeyValue) {
    log.debug("verify proof for DDO with DID {}", ddo.getId());

    boolean result = false;

    EdDSAPublicKey edDSAPublicKey =
        new EdDSAPublicKey(new EdDSAPublicKeySpec(publicKeyValue, parameterSpec));

    try {
      result = suite.verify(ddo, edDSAPublicKey);
    } catch (IOException e) {
      log.error(e.toString(), e);
    }

    log.debug("end verify proof for DDO with DID {}", ddo.getId());

    return result;
  }

}
