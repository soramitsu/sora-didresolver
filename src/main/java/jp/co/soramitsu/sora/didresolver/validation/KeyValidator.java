package jp.co.soramitsu.sora.didresolver.validation;

import static java.util.regex.Pattern.compile;

import java.net.URI;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import jp.co.soramitsu.sora.didresolver.validation.constrains.KeyConstraint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyValidator implements ConstraintValidator<KeyConstraint, URI> {

  private static final Pattern COMPILED_PATTERN = compile("keys-\\d+");

  @Override
  public boolean isValid(URI publicKey, ConstraintValidatorContext context) {
    log.debug("validation format of public key - {}", publicKey);
    String did = publicKey.getScheme() + ":" + publicKey.getSchemeSpecificPart();
    String fragment = publicKey.getFragment();
    boolean isValid =
        new DIDValidator().isValid(did, context) && fragment != null && COMPILED_PATTERN
            .matcher(fragment).matches();
    log.debug("result of validation format of public key - {} is {}", publicKey, isValid);
    return isValid;
  }
}
