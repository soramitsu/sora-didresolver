package jp.co.soramitsu.sora.didresolver.domain.valueobjects;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.upperCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDUnparseableException;
import lombok.Value;

@Value
public class DID {

  private static final String TYPE = "TYPE";
  private static String didTypesConcat = stream(DIDTypeEnum.values())
      .map(Enum::name)
      .map(String::toLowerCase)
      .reduce((type1, type2) -> type1 + "|" + type2)
      .get();
  public static final Pattern didPattern = Pattern.compile(
      "did:sora:(?<TYPE>" + didTypesConcat + "):.+");

  private DIDTypeEnum type;
  private String identifier;
  private String didString;

  @Override
  public String toString() {
    return didString;
  }

  public DID(String did) throws DIDUnparseableException {
    if (did != null) {

      Matcher matcher = didPattern.matcher(did);
      if (!matcher.matches()) {
        throw new DIDUnparseableException(did);
      }

      String typeString = matcher.group(TYPE);

      this.type = DIDTypeEnum.valueOf(upperCase(typeString));
      this.identifier = type.identifier(did);
      this.didString = did;
    } else {
      throw new IllegalArgumentException(
          "Null is not permitted for " + DID.class.getName() + " constructor");
    }
  }

}
