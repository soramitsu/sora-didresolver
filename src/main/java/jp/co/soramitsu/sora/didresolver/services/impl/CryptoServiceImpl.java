package jp.co.soramitsu.sora.didresolver.services.impl;

import java.util.List;
import java.util.Optional;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.sora.crypto.Crypto;
import jp.co.soramitsu.sora.crypto.Crypto.CreateVerifyHashException;
import jp.co.soramitsu.sora.crypto.Crypto.NoSuchStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.hash.Sha3_256;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.dto.Proof;
import jp.co.soramitsu.sora.didresolver.dto.PublicKey;
import jp.co.soramitsu.sora.didresolver.services.CryptoService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {

  @Override
  public boolean verifyDDOProof(DDO ddo, byte[] publicKeyBytes) {
    log.debug("verify proof for DDO with DID {}", ddo.getId());
    boolean verifyResult = false;
    try {
      verifyResult = getCrypto().verifyAll(ddo, Ed25519Sha3.publicKeyFromBytes(publicKeyBytes));
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

  private Crypto getCrypto() {
    return new Crypto(new Sha3_256());
  }
}
