package jp.co.soramitsu.sora.validation;

import java.util.Set;
import javax.validation.ConstraintViolation;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CryptoTypeValidatorTest extends BaseValidatorTest {

  @Test
  public void testValidDDO() {
    Set<ConstraintViolation<DDO>> constraintViolations = validator.validate(ddo);
    Assert.assertTrue(constraintViolations.isEmpty());
  }

  @ParameterizedTest
  @CsvSource({"invalidCryptoType, 1", "null, 1", "Ed25519Sha3VerificationKey, 1",
      "Ed25519Sha3Authentication, 1"})
  public void testInvalidCryptoTypeOnProof(String cryptoTypeValue,
      int expectedConstraintViolations) {
    ddo.getProof().forEach(proof -> proof.setType(cryptoTypeValue));
    Set<ConstraintViolation<DDO>> constraintViolations = validator.validate(ddo);
    Assert.assertEquals(expectedConstraintViolations, constraintViolations.size());
  }

  @ParameterizedTest
  @CsvSource({"invalidCryptoType, 2", "null, 2", "Ed25519Sha3Signature, 2",
      "Ed25519Sha3Authentication, 2"})
  public void testInvalidCryptoTypeOnPublicKey(String cryptoTypeValue,
      int expectedConstraintViolations) {
    ddo.getPublicKey().forEach(publicKey -> publicKey.setType(cryptoTypeValue));
    Set<ConstraintViolation<DDO>> constraintViolations = validator.validate(ddo);
    Assert.assertEquals(expectedConstraintViolations, constraintViolations.size());
  }

  @ParameterizedTest
  @CsvSource({"invalidCryptoType, 1", "null, 1", "Ed25519Sha3Signature, 1",
      "Ed25519Sha3VerificationKey, 1"})
  public void testInvalidCryptoTypeOnAuthentication(String cryptoTypeValue,
      int expectedConstraintViolations) {
    ddo.getAuthentication().forEach(authentication -> authentication.setType(cryptoTypeValue));
    Set<ConstraintViolation<DDO>> constraintViolations = validator.validate(ddo);
    Assert.assertEquals(expectedConstraintViolations, constraintViolations.size());
  }
}
