package jp.co.soramitsu.sora.didresolver.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import jp.co.soramitsu.sora.crypto.Consts;
import jp.co.soramitsu.sora.didresolver.validation.constrains.KeyConstraint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyValidator implements ConstraintValidator<KeyConstraint, String> {

  private static final String KEY_FORMAT_PATTERN = ".*" + Consts.DID_URI_DETERMINATOR + "keys-\\d";

  @Override
  public boolean isValid(String publicKey, ConstraintValidatorContext context) {
    log.debug("validation format of public key - {}", publicKey);
    boolean isValid = false;
    if (publicKey.matches(KEY_FORMAT_PATTERN)) {
      String did = publicKey.substring(0, publicKey.indexOf(Consts.DID_URI_DETERMINATOR));
      isValid = new DIDValidator().isValid(did, context);
    }
    log.debug("result of validation format of public key - {} is {}", publicKey, isValid);
    return isValid;
  }
}
