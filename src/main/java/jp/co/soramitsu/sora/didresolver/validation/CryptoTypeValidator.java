package jp.co.soramitsu.sora.didresolver.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import jp.co.soramitsu.sora.didresolver.commons.CryptoActionTypeEnum;
import jp.co.soramitsu.sora.didresolver.commons.CryptoTypeEnum;
import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoTypeValidator implements ConstraintValidator<CryptoTypeConstraint, String> {

  private CryptoActionTypeEnum cryptoActionTypeEnum;

  @Override
  public void initialize(CryptoTypeConstraint constraintAnnotation) {
    this.cryptoActionTypeEnum = constraintAnnotation.cryptoTypeEnum();
  }

  public boolean isValid(String value, ConstraintValidatorContext context) {
    log.debug("validation of crypto type - {}", value);
    boolean isValid = false;
    try {
      isValid = CryptoTypeEnum.getCryptoTypes(cryptoActionTypeEnum).contains(value);
    } catch (NullPointerException | IllegalArgumentException ignored) {
    }
    log.debug("result of validation crypto type - {} is {}", value, isValid);
    return isValid;
  }
}
