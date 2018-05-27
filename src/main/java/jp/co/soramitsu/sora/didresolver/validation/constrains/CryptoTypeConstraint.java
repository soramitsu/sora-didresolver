package jp.co.soramitsu.sora.didresolver.validation.constrains;

import jp.co.soramitsu.sora.didresolver.validation.CryptoTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author rogachevsn
 */
@Documented
@Constraint(validatedBy = CryptoTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CryptoTypeConstraint {
    String message() default "Invalid signature type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
