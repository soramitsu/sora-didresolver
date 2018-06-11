package jp.co.soramitsu.sora.didresolver.domain.valueobjects;

import static java.util.regex.Pattern.compile;
import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.IROHA;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.co.soramitsu.sora.didresolver.exceptions.IrohaIdentifierUnparseableException;
import lombok.Value;

/**
 * Unneeded class which was due to incorrect assumption, that DDO will be retrieved knowing account id
 * But it might prove itself useful later on
 * */
@Value
public class IrohaIdentifier {

  private static final Pattern irohaPattern = compile(IROHA.getRegexp());
  public static final String ACCOUNT = "ACCOUNT";
  public static final String DOMAIN = "DOMAIN";

  private String accountId;
  private String domainId;
  private String identifier;

  @Override
  public String toString() {
    return identifier;
  }

  public IrohaIdentifier(String identifier) throws IrohaIdentifierUnparseableException {
    Matcher bothAccountAndDomainMatcher = irohaPattern.matcher(identifier);
    if (!bothAccountAndDomainMatcher.matches()) {
      throw new IrohaIdentifierUnparseableException(identifier);
    } else {
      accountId = bothAccountAndDomainMatcher.group(ACCOUNT);
      domainId = bothAccountAndDomainMatcher.group(DOMAIN);
    }

    this.identifier = identifier;
  }

}
