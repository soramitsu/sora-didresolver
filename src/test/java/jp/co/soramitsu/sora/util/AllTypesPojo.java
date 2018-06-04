package jp.co.soramitsu.sora.util;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AllTypesPojo {

  private int primitiveInt;
  private double primitiveDouble;
  private Object nullable;
  private String string;
  private Double floating;
  private Integer integer;
  private Boolean bool;
  private List<String> listOfStrings;
  private Map<String, Object> map;
  private Instant time;
}
