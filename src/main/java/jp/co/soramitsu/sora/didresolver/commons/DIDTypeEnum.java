package jp.co.soramitsu.sora.didresolver.commons;

import java.util.regex.Pattern;
import lombok.Getter;

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
}
