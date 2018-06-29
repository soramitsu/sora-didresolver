package jp.co.soramitsu.sora.didresolver.services.impl;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.sora.crypto.Consts;
import jp.co.soramitsu.sora.crypto.Crypto;
import jp.co.soramitsu.sora.crypto.Crypto.CreateVerifyHashException;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.algorithms.SignatureSuiteRegistry.NoSuchStrategy;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;
import jp.co.soramitsu.sora.didresolver.services.CryptoService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {

  private Crypto crypto;

  @Override
  public boolean verifyDDOProof(DDO ddo, byte[] publicKeyBytes) {
    log.debug("verify proof for DDO with DID {}", ddo.getId());
    boolean verifyResult = false;
    try {
      verifyResult = crypto.verifyAll(ddo, Ed25519Sha3.publicKeyFromBytes(publicKeyBytes));
    } catch (CreateVerifyHashException | SignatureSuiteException | NoSuchStrategy e) {
      log.error(e.toString(), e);
    }
    log.debug("end verify proof for DDO with DID {}", ddo.getId());
    return verifyResult;
  }

  @Override
  public boolean checkProofCorrectness(@NotNull @Valid Proof proof, @NotBlank String did,
      @NotNull @Valid List<PublicKey> publicKeys) {
    String proofDID = proof.getCreator()
        .substring(0, proof.getCreator().indexOf(Consts.DID_URI_DETERMINATOR));
    log.debug("check proof correctness for Proof: proofDID {}, DID {}", proofDID, did);
    return proofDID.equals(did) && isProofInPublicKeys(proof, publicKeys);
  }

  @Override
  public PublicKey getPublicKeyByProof(Proof proof, List<PublicKey> publicKeys) {
    log.debug("get public key for proof {}", proof.getCreator());
    return publicKeys.stream().filter(key -> proof.getCreator().equals(key.getId())).findFirst().get();
  }

  private boolean isProofInPublicKeys(Proof proof, List<PublicKey> publicKeys) {
    return publicKeys.stream().anyMatch(key -> proof.getCreator().equals(key.getId()));
  }
}
