package jp.co.soramitsu.sora.didresolver.services.impl;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.sora.didresolver.dto.Authentication;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;
import jp.co.soramitsu.sora.didresolver.services.ValidateService;

public class ValidateServiceImpl implements ValidateService {

  @Override
  public boolean isProofInPublicKeys(@NotBlank String proofCreator, List<PublicKey> publicKeys) {
    return publicKeys.stream().anyMatch(key -> proofCreator.equals(key.getId()));
  }

  @Override
  public boolean isProofCreatorInAuth(@NotBlank String creator,
      @NotNull @Valid List<Authentication> authentication) {
    return authentication.stream().anyMatch(auth -> auth.getPublicKey().equals(creator));
  }
}