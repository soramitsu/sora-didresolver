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
  @CsvSource({"invalidCryptoType, 1", "null, 1"})
  public void testInvalidCryptoType(String cryptoTypeValue, int expectedConstraintViolations) {
    System.out.println("testInvalidCryptoType");
    ddo.getProof().forEach(proof -> proof.setType(cryptoTypeValue));
    Set<ConstraintViolation<DDO>> constraintViolations = validator.validate(ddo);
    Assert.assertEquals(constraintViolations.size(), expectedConstraintViolations);
  }
}
