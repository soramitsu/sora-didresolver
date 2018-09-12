package jp.co.soramitsu.sora.didresolver.commons;

import java.util.regex.Pattern;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDUnparseableException;
import lombok.Getter;
import lombok.val;

public enum DIDTypeEnum {
  USERNAME("[a-zA-Z0-9]{6,32}");

  @Getter
  private String regexp;

  @Getter
  private Pattern pattern;

  DIDTypeEnum(String regexp) {
    this.regexp = regexp;
    this.pattern = Pattern.compile("did:sora:" + "(?<IDENTIFIER>" + getRegexp() + ")");
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
