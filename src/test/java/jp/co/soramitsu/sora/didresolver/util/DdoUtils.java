package jp.co.soramitsu.sora.didresolver.util;

import static java.time.Instant.now;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.authentication.Ed25519Sha3Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.service.GenericService;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;

public final class DdoUtils {
  public static final String DEFAULT_DID = "did:sora:user123";

  public static DDO getDefaultDdo() throws ParserException, MalformedURLException {
    final DID did = DID.parse(DEFAULT_DID);
    Ed25519Sha3 ed25519Sha3 = new Ed25519Sha3();
    return DDO.builder()
        .authentication(new Ed25519Sha3Authentication(did.withPath("keys").withFragment("keys-1")))
        .id(did)
        .created(now())
        .service(new GenericService(did, new URL("http://google.com/")))
        .build();
  }
}
