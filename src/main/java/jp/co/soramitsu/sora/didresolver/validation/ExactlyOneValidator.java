package jp.co.soramitsu.sora.didresolver.validation;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import jp.co.soramitsu.sora.didresolver.validation.constrains.ExactlyOneConstraint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;

@Slf4j
public class ExactlyOneValidator implements ConstraintValidator<ExactlyOneConstraint, Object> {

  private String[] group;

  @Override
  public void initialize(ExactlyOneConstraint constraintAnnotation) {
    group = constraintAnnotation.group();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    log.debug("check that only one field from the associated set of fields is filled in ");
    boolean isValid = false;
    if (group != null) {
      List<Object> fieldValues = new ArrayList<>();
      for (String element : group) {
        Object propValue = new BeanWrapperImpl(value).getPropertyValue(element);
        if (propValue != null) {
          log.debug("ExactlyOneValidator: element - " + element + "; value - " + propValue);
          fieldValues.add(propValue);
        }
      }
      isValid = fieldValues.size() == 1;
    }
    log.debug("result of ExactlyOneValidator is " + isValid);
    return isValid;
  }
}
