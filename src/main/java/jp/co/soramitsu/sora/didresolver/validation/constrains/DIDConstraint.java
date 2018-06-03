package jp.co.soramitsu.sora.didresolver.validation.constrains;

import jp.co.soramitsu.sora.didresolver.validation.DIDValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DIDValidator.class)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DIDConstraint {
    String message() default "Invalid DID format";

    boolean isNullable();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
