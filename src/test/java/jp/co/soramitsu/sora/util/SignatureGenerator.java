package jp.co.soramitsu.sora.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.KeyPair;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.CryptoException;
import jp.co.soramitsu.crypto.ed25519.EdDSAPrivateKey;
import jp.co.soramitsu.crypto.ed25519.EdDSAPublicKey;
import jp.co.soramitsu.sora.crypto.DocumentSignatureService;
import jp.co.soramitsu.sora.crypto.DocumentSignatureService.NoSuchStrategy;
import jp.co.soramitsu.sora.crypto.algorithms.RawSignatureStrategy.SignatureSuiteException;
import jp.co.soramitsu.sora.crypto.hash.Sha3Digest256;
import jp.co.soramitsu.sora.didresolver.dto.DDO;

public class SignatureGenerator {

  private static final String PATH = "src/test/resources/";

  public static void main(String[] args)
      throws IOException, SignatureSuiteException, NoSuchStrategy, CryptoException {
    DocumentSignatureService documentSignatureService = new DocumentSignatureService(
        new Sha3Digest256());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    Reader jsonReader = new BufferedReader(
        new InputStreamReader(
            SignatureGenerator.class.getClassLoader().getResourceAsStream("ddo.json")));
    DDO ddo = objectMapper.readValue(jsonReader, DDO.class);
    KeyPair keyPair = new Ed25519Sha3().generateKeypair();
    writeKeyPairToFile(keyPair);
    ddo.getPublicKey().get(1).setPublicKeyValue(((EdDSAPublicKey) keyPair.getPublic()).getAbyte());
    documentSignatureService.sign(ddo, keyPair, ddo.getProof());
    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(PATH + "canonicalDDO.json")))) {
      objectMapper.writeValue(writer, ddo);
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
