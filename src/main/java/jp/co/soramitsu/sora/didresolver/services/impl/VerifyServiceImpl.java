package jp.co.soramitsu.sora.didresolver.services.impl;

import java.util.List;
import java.util.Optional;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.sora.crypto.DocumentSignatureService;
import jp.co.soramitsu.sora.crypto.DocumentSignatureService.CreateVerifyHashException;
import jp.co.soramitsu.sora.crypto.DocumentSignatureService.NoSuchStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.hash.Sha3Digest256;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
@Slf4j
public class VerifyServiceImpl implements VerifyService {

  @Override
  public boolean verifyDDOProof(DDO ddo, byte[] publicKeyBytes) {
    log.debug("verify proof for DDO with DID {}", ddo.getId());
    boolean verifyResult = false;
    try {
      verifyResult = getSignatureService()
          .verify(ddo, Ed25519Sha3.publicKeyFromBytes(publicKeyBytes), ddo.getProof());
    } catch (CreateVerifyHashException | SignatureSuiteException | NoSuchStrategy e) {
      log.error(e.toString(), e);
    }
    log.debug("end verify proof for DDO with DID {}", ddo.getId());
    return verifyResult;
  }

  @Override
  public Optional<PublicKey> getPublicKeyByProof(Proof proof, List<PublicKey> publicKeys) {
    log.debug("get public key for proof {}", proof.getCreator());
    return publicKeys.stream().filter(key -> proof.getCreator().equals(key.getId())).findFirst();
  }

  private DocumentSignatureService getSignatureService() {
    return new DocumentSignatureService(new Sha3Digest256());
  }
}
