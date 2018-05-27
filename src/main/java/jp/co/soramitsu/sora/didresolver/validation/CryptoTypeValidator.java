package jp.co.soramitsu.sora.didresolver.validation;

import jp.co.soramitsu.sora.didresolver.commons.CryptoTypeEnum;
import jp.co.soramitsu.sora.didresolver.validation.constrains.CryptoTypeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author rogachevsn
 */
public class CryptoTypeValidator implements ConstraintValidator<CryptoTypeConstraint, String> {

    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = false;
        try {
            CryptoTypeEnum.valueOf(value);
            isValid = true;
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }
        return isValid;
    }
}
