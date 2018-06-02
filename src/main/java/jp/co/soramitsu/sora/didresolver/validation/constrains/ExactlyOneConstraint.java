package jp.co.soramitsu.sora.didresolver.validation.constrains;

import jp.co.soramitsu.sora.didresolver.validation.ExactlyOneValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExactlyOneValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExactlyOneConstraint {
    String message() default "Object must include exactly one value property";

    String[] group();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ExactlyOneConstraint[] value();
    }
}
