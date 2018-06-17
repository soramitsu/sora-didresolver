package jp.co.soramitsu.sora.didresolver.validation.constrains;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import jp.co.soramitsu.sora.didresolver.validation.CryptoTypeValidator;

@Documented
@Constraint(validatedBy = CryptoTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * Constraint for validation allowed algorithms of the signature
 */
public @interface CryptoTypeConstraint {

  String message() default "Invalid signature type";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
