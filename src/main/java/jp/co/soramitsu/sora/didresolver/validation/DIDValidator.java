package jp.co.soramitsu.sora.didresolver.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class DIDValidator implements ConstraintValidator<DIDConstraint, String> {

  //TODO move to application properties
  private static final String DID_STRUCT_REGEXP = "did:sora:\\w*:.*";
  private static final char DELIMITER = ':';

  private boolean isNullable;

  @Override
  public void initialize(DIDConstraint constraintAnnotation) {
    this.isNullable = constraintAnnotation.isNullable();
  }

  @Override
  public boolean isValid(String did, ConstraintValidatorContext context) {
    log.debug("validation format of DID - {}", did);
    boolean isValid = isNullable;
    if (StringUtils.isNotBlank(did) && did.matches(DID_STRUCT_REGEXP)) {
      String[] didParts = StringUtils.split(did, DELIMITER);
      isValid = didParts.length > 3 && checkTypeAndIdentifier(didParts[2], didParts[3]);
    }
    log.debug("result of validation format of DID - {} is {}", did, isValid);
    return isValid;
  }

  private boolean checkTypeAndIdentifier(String didType, String identifier) {
    boolean checkResult = false;
    try {
      checkResult = identifier.matches(DIDTypeEnum.valueOf(didType.toUpperCase()).getRegexp());
    } catch (NullPointerException | IllegalArgumentException ignored) {
    }
    return checkResult;
  }

}
