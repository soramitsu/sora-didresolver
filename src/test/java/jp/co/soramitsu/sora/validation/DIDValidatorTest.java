package jp.co.soramitsu.sora.validation;

import java.util.Set;
import javax.validation.ConstraintViolation;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class DIDValidatorTest extends BaseValidatorTest {

  @Test
  public void testValidDID() {
    Set<ConstraintViolation<DDO>> constraintViolations = validator.validate(ddo);
    Assert.assertTrue(constraintViolations.isEmpty());
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/incorrectDids.csv", numLinesToSkip = 1)
  public void testInvalidDID(String did) {
    ddo.setId(did);
    Set<ConstraintViolation<DDO>> constraintViolations = validator.validate(ddo);
    Assert.assertEquals(constraintViolations.size(), 1);
  }
}
