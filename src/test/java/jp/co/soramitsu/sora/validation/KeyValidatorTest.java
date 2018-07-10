package jp.co.soramitsu.sora.validation;

import java.util.Set;
import javax.validation.ConstraintViolation;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class KeyValidatorTest extends BaseValidatorTest {

  @ParameterizedTest
  @CsvFileSource(resources = {"/incorrectDids.csv", "/incorrectKeys.csv"}, numLinesToSkip = 1)
  public void testInvalid(String did) {
    ddo.setId(did);
    Set<ConstraintViolation<DDO>> constraintViolations = validator.validate(ddo);
    Assert.assertEquals(constraintViolations.size(), 1);
  }
}
