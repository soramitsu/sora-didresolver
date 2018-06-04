package jp.co.soramitsu.sora.util;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AllTypesPojo {

  int primitiveInt;
  double primitiveDouble;
  Object nullable;
  String string;
  Double floating;
  Integer integer;
  Boolean bool;
  List<String> listOfStrings;
  Map<String, Object> map;
  Instant time;
}
