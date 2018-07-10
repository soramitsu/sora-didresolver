package jp.co.soramitsu.sora.didresolver.validation.constrains;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import jp.co.soramitsu.sora.didresolver.validation.KeyValidator;

@Documented
@Constraint(validatedBy = KeyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
/*
 * Constraint for validation public key field format
 */
public @interface KeyConstraint {

  String message() default "Invalid public key format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
