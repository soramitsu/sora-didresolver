package jp.co.soramitsu.sora.crypto.algorithms;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SignatureSuiteRegistry {
  INSTANCE;

  private Map<String, RawSignatureStrategy> algorithms = new HashMap<>();

  public void register(String key, RawSignatureStrategy val) {
    log.debug("register signature strategy with key - {} and value - {}", key, val.toString());
    algorithms.put(key, val);
  }

  public void deregister(String key) {
    log.debug("remove signature strategy with key - {}", key);
    algorithms.remove(key);
  }

  public RawSignatureStrategy get(String key) throws NoSuchStrategy {
    log.debug("find appropriate digital signature algorithm by key - {}", key);
    // find appropriate digital signature algorithm
    if (!has(key)) {
      log.warn("{} signature suite is not implemented", key);
      throw new NoSuchStrategy(key);
    }
    log.debug("get raw signature startegy - {} by key - {}", algorithms.get(key), key);
    return algorithms.get(key);
  }

  public boolean has(String key) {
    return algorithms.containsKey(key);
  }

  public void clear() {
    log.debug("clear algorithms storage");
    algorithms.clear();
  }

  public static class NoSuchStrategy extends Exception {

    public NoSuchStrategy(String key) {
      super(key + "signature suite is not implemented");
    }
  }

}
