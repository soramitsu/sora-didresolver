package jp.co.soramitsu.sora.didresolver.validation.constrains;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Payload;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
/**
 * Constraint for validation DID format
 */
public @interface DIDConstraint {

  String message() default "Invalid DID format";

  boolean isNullable();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
