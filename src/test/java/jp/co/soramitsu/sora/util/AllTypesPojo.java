package jp.co.soramitsu.sora.util;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import jp.co.soramitsu.sora.crypto.MapSerializable;
import lombok.Builder;
import lombok.Getter;

@Builder
public class AllTypesPojo implements MapSerializable {

  @Getter
  Object nullable;
  @Getter
  String string;
  @Getter
  Integer integer;
  @Getter
  Boolean bool;
  @Getter
  List<String> listOfStrings;
  @Getter
  Map<String, Object> map;
  @Getter
  Instant time;
}
