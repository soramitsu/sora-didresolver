package jp.co.soramitsu.sora.didresolver.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import jp.co.soramitsu.sora.didresolver.commons.CryptoTypeEnum;
import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoTypeValidator implements ConstraintValidator<CryptoTypeConstraint, String> {

  public boolean isValid(String value, ConstraintValidatorContext context) {
    log.debug("validation of crypto type - " + value);
    boolean isValid = false;
    try {
      CryptoTypeEnum.valueOf(value);
      isValid = true;
    } catch (NullPointerException | IllegalArgumentException ignored) {
    }
    log.debug("result of validation crypto type - " + value + " is " + isValid);
    return isValid;
  }
}
