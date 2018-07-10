package jp.co.soramitsu.sora.didresolver.validation;

import java.net.URI;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import jp.co.soramitsu.sora.didresolver.validation.constrains.KeyConstraint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyValidator implements ConstraintValidator<KeyConstraint, URI> {

  private static final String KEY_FRAGMENT_PATTERN = "keys-\\d+";

  @Override
  public boolean isValid(URI publicKey, ConstraintValidatorContext context) {
    log.debug("validation format of public key - {}", publicKey);
    String did = publicKey.getScheme() + ":" + publicKey.getSchemeSpecificPart();
    String fragment = publicKey.getFragment();
    boolean isValid = new DIDValidator().isValid(did, context) && fragment != null && fragment
        .matches(KEY_FRAGMENT_PATTERN);
    log.debug("result of validation format of public key - {} is {}", publicKey, isValid);
    return isValid;
  }
}
