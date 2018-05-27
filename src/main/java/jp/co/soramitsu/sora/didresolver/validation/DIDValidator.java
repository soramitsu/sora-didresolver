package jp.co.soramitsu.sora.didresolver.validation;

import jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum;
import jp.co.soramitsu.sora.didresolver.validation.constrains.DIDConstraint;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author rogachevsn
 */
public class DIDValidator implements ConstraintValidator<DIDConstraint, String> {

    //TODO move to application properties
    private static final String DID_STRUCT_REGEXP = "did:sora:\\w*:.*";
    private static final char DELIMETER = ':';

    private boolean isNullable;

    @Override
    public void initialize(DIDConstraint constraintAnnotation) {
        this.isNullable = constraintAnnotation.isNullable();
    }

    @Override
    public boolean isValid(String did, ConstraintValidatorContext context) {
        boolean isValid = isNullable;
        if (StringUtils.isNotBlank(did) && did.matches(DID_STRUCT_REGEXP)) {
            String[] didParts = StringUtils.split(did, DELIMETER);
            isValid = checkTypeAndIdentifier(didParts[2], didParts[3]);
        }
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
