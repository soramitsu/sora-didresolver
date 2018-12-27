package jp.co.soramitsu.sora.didresolver.validation;

import static java.util.regex.Pattern.compile;
import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.USERNAME;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class DIDValidator implements ConstraintValidator<DIDConstraint, String> {

  private static final String DID_STRUCT_REGEXP = "did:sora:.+";
  private static final char DELIMITER = ':';
  private static final Pattern DID_PATTERN = compile(DID_STRUCT_REGEXP);
  private static final Pattern IDENTIFIER_PATTERN = compile(USERNAME.getRegexp());
  private static final int THIRD_DID_PART = 2;

  private boolean isNullable;

  @Override
  public void initialize(DIDConstraint constraintAnnotation) {
    this.isNullable = constraintAnnotation.isNullable();
  }

  @Override
  public boolean isValid(String did, ConstraintValidatorContext context) {
    log.debug("validation format of DID - {}", did);
    boolean isValid = isNullable;
    if (StringUtils.isNotBlank(did) && DID_PATTERN.matcher(did).matches()) {
      val didParts = StringUtils.split(did, DELIMITER);
      isValid = checkTypeAndIdentifier(didParts[THIRD_DID_PART]);
    }
    log.debug("result of validation format of DID - {} is {}", did, isValid);
    return isValid;
  }

  private static boolean checkTypeAndIdentifier(String identifier) {
    return IDENTIFIER_PATTERN.matcher(identifier).matches();
  }

}
