package jp.co.soramitsu.sora.didresolver.validation;

import jp.co.soramitsu.sora.didresolver.validation.constrains.ExactlyOneConstraint;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ExactlyOneValidator implements ConstraintValidator<ExactlyOneConstraint, Object> {

    private String[] group;

    @Override
    public void initialize(ExactlyOneConstraint constraintAnnotation) {
        group = constraintAnnotation.group();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean isValid = false;
        if (group != null) {
            List<Object> fieldValues = new ArrayList<>();
            for (String element : group) {
                Object propValue = new BeanWrapperImpl(value).getPropertyValue(element);
                if (propValue != null) {
                    fieldValues.add(propValue);
                }
            }
            isValid = fieldValues.size() == 1;
        }
        return isValid;
    }
}
