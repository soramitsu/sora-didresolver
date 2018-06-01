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
  public Object nullable;
  @Getter
  public String string;
  @Getter
  public Integer integer;
  @Getter
  public Boolean bool;
  @Getter
  public List<String> listOfStrings;
  @Getter
  public Map<String, Object> map;
  @Getter
  public Instant time;
}
