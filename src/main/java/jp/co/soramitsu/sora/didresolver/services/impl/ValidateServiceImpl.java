package jp.co.soramitsu.sora.didresolver.services.impl;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.didresolver.services.ValidateService;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
import org.springframework.stereotype.Service;

@Service
public class ValidateServiceImpl implements ValidateService {

  @Override
  public boolean isProofInPublicKeys(@NotNull DID proofCreator, List<PublicKey> publicKeys) {
    return publicKeys.stream().anyMatch(key -> proofCreator.equals(key.getId()));
  }

  @Override
  public boolean isProofCreatorInAuth(
      @NotNull DID creator, @NotNull @Valid List<Authentication> authentication) {
    return authentication.stream().anyMatch(auth -> auth.getPublicKey().equals(creator));
  }
}
