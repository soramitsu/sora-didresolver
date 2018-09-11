package jp.co.soramitsu.sora.util;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static jp.co.soramitsu.crypto.ed25519.EdDSAKey.KEY_ALGORITHM;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import jp.co.soramitsu.crypto.ed25519.EdDSAPrivateKey;
import jp.co.soramitsu.crypto.ed25519.EdDSAPublicKey;
import jp.co.soramitsu.sora.crypto.common.SecurityProvider;
import jp.co.soramitsu.sora.crypto.json.JSONCanonizerWithOneCoding;
import jp.co.soramitsu.sora.crypto.proof.Options;
import jp.co.soramitsu.sora.crypto.signature.suite.JSONEd25519Sha3SignatureSuite;
import jp.co.soramitsu.sora.crypto.type.SignatureTypeEnum;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import lombok.val;

public class SignatureGenerator {

  private static final String PATH = "src/test/resources/";

  public static void main(String[] args)
      throws IOException, SignatureException, NoSuchAlgorithmException {

    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .enable(INDENT_OUTPUT)
        .setSerializationInclusion(NON_NULL);

    JSONEd25519Sha3SignatureSuite documentSignatureService = new JSONEd25519Sha3SignatureSuite(
        new SecurityProvider(), new JSONCanonizerWithOneCoding(), objectMapper);

    InputStream in = SignatureGenerator.class.getClassLoader().getResourceAsStream("ddo.json");
    DDO ddo = objectMapper.readValue(in, DDO.class);

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    writeKeyPairToFile(keyPair);

    ddo.getPublicKey()
        .get(0)
        .setId(DID.randomUUID());
//        .setId(keyPair.getPublic().getEncoded());

    Proof proof = ddo.getProof();
    Options options = new Options(
        SignatureTypeEnum.valueOf(proof.getOptions().getType().getSignatureType()),
        proof.getOptions().getCreated().toString(),
        proof.getOptions().getCreator().toString(),
        proof.getOptions().getNonce(),
        proof.getOptions().getPurpose()
    );

    val signed = documentSignatureService
        .sign(ddo, (EdDSAPrivateKey) keyPair.getPrivate(), options);

    assert documentSignatureService.verify(signed, (EdDSAPublicKey) keyPair.getPublic());

    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(PATH + "canonicalDDO.json")))) {
      objectMapper
          .writeValue(writer, signed);
    }
    System.out.println(ddo);
  }

  private static void writeKeyPairToFile(KeyPair keyPair) throws IOException {
    try (Writer keyWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(PATH + "keypair.txt")))) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("publicKey : ");
      byte[] publicKey = ((EdDSAPublicKey) keyPair.getPublic()).getAbyte();
      stringBuilder.append(Arrays.toString(publicKey));
      stringBuilder.append("\n");
      stringBuilder.append("publicKeyHex : ");
      stringBuilder.append(DatatypeConverter.printHexBinary(publicKey));
      stringBuilder.append("\n");
      stringBuilder.append("privateKey : ");
      byte[] privateKey = ((EdDSAPrivateKey) keyPair.getPrivate()).geta();
      stringBuilder.append(Arrays.toString(privateKey));
      stringBuilder.append("\n");
      stringBuilder.append("privateKeyHex : ");
      stringBuilder.append(DatatypeConverter.printHexBinary(privateKey));
      keyWriter.write(stringBuilder.toString());
    }
  }
}
