package jp.co.soramitsu.sora.didresolver.domain.iroha.valueobjects;

import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.ED;
import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.IROHA;
import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.UUID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDUnparseableException;
import lombok.Value;

@Value
public class DID {

  private static final String IROHA_REGEX = IROHA.getRegexp();
  private static final String UUID_REGEX = UUID.getRegexp();
  private static final String ED_REGEX = ED.getRegexp();

  public static final Pattern didPattern = Pattern.compile(
      "did:sora:(?<TYPE>iroha|uuid|ed):.+");
  private static final Pattern irohaPattern = Pattern
      .compile("did:sora:iroha:(?<IDENTIFIER>" + IROHA_REGEX + ")");
  private static final Pattern uuidPattern = Pattern
      .compile("did:sora:uuid:(?<IDENTIFIER>" + UUID_REGEX + ")");
  private static final Pattern edPattern = Pattern
      .compile("did:sora:ed:(?<IDENTIFIER>" + ED_REGEX + ")");


  DIDTypeEnum type;
  String identifier;
  String didString;

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

      String typeString = matcher.group("TYPE");

      switch (typeString) {
        case "iroha":
          this.type = IROHA;
          this.identifier = getIdentifier(did, irohaPattern.matcher(did));
          break;
        case "uuid":
          this.type = UUID;
          this.identifier = getIdentifier(did, uuidPattern.matcher(did));
          break;
        case "ed":
          this.type = ED;
          this.identifier = getIdentifier(did, edPattern.matcher(did));
          break;
        default:
          throw new IllegalStateException(
              "DID type pattern matched, but identifier pattern never did!!!");
      }

      this.didString = did;
    } else {
      throw new NullPointerException(
          "Null is not permitted for " + DID.class.getName() + " constructor");
    }
  }

  private String getIdentifier(String did, Matcher matcher) throws DIDUnparseableException {
    if (matcher.matches()) {
      return matcher.group("IDENTIFIER");
    } else {
      throw new DIDUnparseableException(did);
    }
  }

}
