package jp.co.soramitsu.sora.didresolver.controllers;

import static java.time.Instant.now;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static jp.co.soramitsu.iroha.java.Utils.parseHexKeypair;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.DID_DUPLICATE;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.DID_IS_TOO_LONG;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.DID_NOT_FOUND;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.INCORRECT_UPDATE_TIME;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.INVALID_PROOF;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.INVALID_PROOF_SIGNATURE;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.PUBLIC_KEY_VALUE_NOT_PRESENTED;
import static jp.co.soramitsu.sora.sdk.did.model.dto.DID.parse;
import static jp.co.soramitsu.sora.sdk.did.model.dto.Options.builder;
import static jp.co.soramitsu.sora.sdk.did.model.type.SignatureTypeEnum.Ed25519Sha3Signature;
import static jp.co.soramitsu.sora.sdk.did.validation.ISO8601DateTimeFormatter.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpStatus.OK;
import static org.testcontainers.shaded.org.bouncycastle.util.encoders.Hex.decode;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyPair;
import java.security.SignatureException;
import javax.annotation.PostConstruct;
import jp.co.soramitsu.crypto.ed25519.EdDSAPrivateKey;
import jp.co.soramitsu.sora.didresolver.IntegrationTest;
import jp.co.soramitsu.sora.didresolver.controllers.dto.GenericResponse;
import jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode;
import jp.co.soramitsu.sora.didresolver.exceptions.DDOUnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.sdk.crypto.common.HexdigestSaltGenerator;
import jp.co.soramitsu.sora.sdk.crypto.common.SaltGenerator;
import jp.co.soramitsu.sora.sdk.crypto.json.JSONEd25519Sha3SignatureSuite;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import jp.co.soramitsu.sora.sdk.did.model.dto.authentication.Ed25519Sha3Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.publickey.Ed25519Sha3VerificationKey;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.ResponseEntity;

public class DIDResolverControllerTest extends IntegrationTest {

  private static final String DEFAULT_LONG_DID = "did:sora:very:long:did:that:is:longer:that:toyota:century:some:test";

  private JacksonTester<DDO> json;
  private DDO ddo;
  private Requests requests;
  private SaltGenerator hexGenerator = new HexdigestSaltGenerator();
  private JSONEd25519Sha3SignatureSuite signature = new JSONEd25519Sha3SignatureSuite();
  private KeyPair keyPair = parseHexKeypair(PUBLICKEY, PRIVATEKEY);

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StorageService storageService;

  @PostConstruct
  void init() {
    requests = new Requests(testRestTemplate);
  }

  @BeforeEach
  void setUp() throws IOException {
    JacksonTester.initFields(this, objectMapper);
    Reader jsonReader = new BufferedReader(
        new InputStreamReader(
            requireNonNull(getClass().getClassLoader().getResourceAsStream("canonical2DDO.json"))));
    ddo = json.read(jsonReader).getObject();
  }

  @Test
  @DisplayName("Successfully gets DDO")
  void getDdo() {
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
    val response = requests.getDDO(ddo.getId());
    assertEquals(OK, response.getStatusCode());
    assertEquals(ResponseCode.OK, getResponseCode(response));
    assertNotNull(response.getBody());
    assertEquals(ddo, response.getBody().getDdo());
  }

  @Test
  @DisplayName("When trying to get DDO which not in Iroha it returns status DID_NOT_FOUND")
  void getDdoDIDNotFound() throws ParserException {
    val response = requests.getDDO(parse("did:sora:wrongkey"));
    assertEquals(OK, response.getStatusCode());
    assertEquals(DID_NOT_FOUND, getResponseCode(response));
  }

  @Test
  @DisplayName("Successfully deletes DDO")
  void deleteDdo() throws DDOUnparseableException {
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
    val response = requests.deleteDDO(ddo.getId());
    assertEquals(OK, response.getStatusCode());
    assertEquals(ResponseCode.OK, getResponseCode(response));
    val ddoFromIroha = storageService.findDDObyDID(ddo.getId().toString()).orElse(null);
    assertNull(ddoFromIroha);
  }

  @Test
  @DisplayName("Successfully updates DDO")
  void updateDDO() throws IOException, SignatureException, DDOUnparseableException {
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
    ddo.setUpdated(format(now()));
    ddo = signDdo(ddo);
    val response = requests.updateDDO(ddo.getId(), ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(ResponseCode.OK, getResponseCode(response));
    val ddoFromIroha = storageService.findDDObyDID(ddo.getId().toString()).orElse(null);
    assertEquals(ddo, ddoFromIroha);
  }

  @Test
  @DisplayName("When trying to update DDO which not in Iroha it returns status DID_NOT_FOUND")
  void updateDdoDIDNotFound() throws IOException, SignatureException, ParserException {
    ddo.setUpdated(format(now()));
    ddo = signDdo(ddo);
    val response = requests.updateDDO(parse("did:sora:wrongkey"), ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(DID_NOT_FOUND, getResponseCode(response));
  }

  @Test
  @DisplayName("When trying to update DDO which doesn't contain public keys it returns status PUBLIC_KEY_VALUE_NOT_PRESENTED")
  void updateDdoCheckPublicKey() {
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
    ddo.setPublicKey(null);
    ddo.setUpdated(format(now()));
    val response = requests.updateDDO(ddo.getId(), ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(PUBLIC_KEY_VALUE_NOT_PRESENTED, getResponseCode(response));
  }

  @Test
  @DisplayName("When trying to update DDO which doesn't contain proof it returns status INVALID_PROOF")
  void updateDdoCheckProof() {
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
    ddo.setProof(null);
    ddo.setUpdated(format(now()));
    val response = requests.updateDDO(ddo.getId(), ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(INVALID_PROOF, getResponseCode(response));
  }

  @Test
  @DisplayName("When trying to update DDO which contain invalid update time it returns status INCORRECT_UPDATE_TIME")
  void updateDdoWrongUpdateDate() throws IOException, SignatureException {
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
    ddo.setUpdated(ddo.getCreated());
    ddo.setCreated(format(now()));
    ddo = signDdo(ddo);
    val response = requests.updateDDO(ddo.getId(), ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(INCORRECT_UPDATE_TIME, getResponseCode(response));
  }

  @Test
  @DisplayName("Successfully creates DDO")
  void createDdo()
      throws ParserException, IOException, SignatureException, DDOUnparseableException {
    var newDdo = createNewDdo();
    newDdo = signDdo(newDdo);
    val response = requests.createDDO(newDdo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(ResponseCode.OK, getResponseCode(response));
    val ddoFromIroha = storageService.findDDObyDID(newDdo.getId().toString()).orElse(null);
    assertEquals(newDdo, ddoFromIroha);
  }

  @Test
  @DisplayName("When trying to create DDO with too long DID it returns status DID_IS_TOO_LONG")
  void createDdoTooLongDID() throws ParserException {
    ddo.setId(parse(DEFAULT_LONG_DID));
    val response = requests.createDDO(ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(DID_IS_TOO_LONG, getResponseCode(response));
  }

  @Test
  @DisplayName("When trying to create DDO which already in Iroha it returns status DID_DUPLICATE")
  void createDdoDuplicate() {
    storageService.createOrUpdate(ddo.getId().toString(), ddo);
    val response = requests.createDDO(ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(DID_DUPLICATE, getResponseCode(response));
  }

  @Test
  @DisplayName("When trying to create DDO which doesn't contain public keys it returns status PUBLIC_KEY_VALUE_NOT_PRESENTED")
  void createDdoCheckPublicKey() {
    ddo.setPublicKey(null);
    val response = requests.createDDO(ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(PUBLIC_KEY_VALUE_NOT_PRESENTED, getResponseCode(response));
  }

  @Test
  @DisplayName("When trying to update DDO which doesn't contain proof it returns status INVALID_PROOF")
  void createDdoCheckProof() {
    ddo.setProof(null);
    val response = requests.createDDO(ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(INVALID_PROOF, getResponseCode(response));
  }

  @Test
  @DisplayName("When trying to create DDO which doesn't contain public key of creator it returns status PUBLIC_KEY_VALUE_NOT_PRESENTED")
  void createDdoCheckCreatorPublicKey() throws ParserException {
    var publicKey = ddo.getPublicKey().get(0);
    publicKey.setId(parse("did:sora:wrongkey#keys-1"));
    ddo.setPublicKey(singletonList(publicKey));
    val response = requests.createDDO(ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(PUBLIC_KEY_VALUE_NOT_PRESENTED, getResponseCode(response));
  }

  @Test
  @DisplayName("When trying to create DDO with invalid signature it returns status INVALID_PROOF_SIGNATURE")
  void createDdoInvalidSignature() {
    val proof = new Proof(ddo.getProof().getOptions(), "wrong proof".getBytes());
    ddo.setProof(proof);
    val response = requests.createDDO(ddo);
    assertEquals(OK, response.getStatusCode());
    assertEquals(INVALID_PROOF_SIGNATURE, getResponseCode(response));
  }

  private ResponseCode getResponseCode(ResponseEntity<? extends GenericResponse> response) {
    return requireNonNull(response.getBody()).getStatus().getCode();
  }

  private DDO signDdo(DDO unsignedDDO) throws IOException, SignatureException {
    val oldPublicKey = unsignedDDO.getPublicKey().get(0);
    var newPublicKey = new Ed25519Sha3VerificationKey(oldPublicKey.getId(), oldPublicKey.getOwner(),
        decode(PUBLICKEY));
    unsignedDDO.setPublicKey(singletonList(newPublicKey));
    val options =
        builder()
            .created(now())
            .creator(newPublicKey.getId())
            .type(Ed25519Sha3Signature)
            .nonce(hexGenerator.next())
            .build();
    return objectMapper
        .treeToValue(signature.sign(unsignedDDO, ((EdDSAPrivateKey) keyPair.getPrivate()), options),
            DDO.class);
  }

  private DDO createNewDdo() throws ParserException {
    val publicKeyId = parse("did:sora:other-username#keys-1");
    return DDO.builder()
        .id(parse("did:sora:other-username"))
        .publicKey(
            new Ed25519Sha3VerificationKey(
                publicKeyId, decode(PUBLICKEY)))
        .authentication(new Ed25519Sha3Authentication(publicKeyId))
        .created(now())
        .build();
  }
}
