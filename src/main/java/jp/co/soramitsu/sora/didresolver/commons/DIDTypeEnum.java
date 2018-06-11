package jp.co.soramitsu.sora.didresolver.commons;

import java.util.regex.Pattern;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDUnparseableException;
import lombok.Getter;
import lombok.val;

public enum DIDTypeEnum {
  IROHA("(?<ACCOUNT>\\w+)@(?<DOMAIN>\\w+[\\w|.]*)"),
  UUID("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");

  @Getter
  private String regexp;

  @Getter
  private Pattern pattern;

  DIDTypeEnum(String regexp) {
    this.regexp = regexp;
    this.pattern = Pattern.compile("did:sora:" + name().toLowerCase() + ":(?<IDENTIFIER>" + getRegexp() + ")");
  }

  public String identifier(String did) throws DIDUnparseableException {
    val matcher = pattern.matcher(did);
    if (matcher.matches()) {
      return matcher.group("IDENTIFIER");
    } else {
      throw new DIDUnparseableException(did);
    }
  }

}
