package jp.co.soramitsu.sora.crypto.algorithms;

import java.util.HashMap;
import java.util.Map;


public enum SignatureSuiteRegistry {
  INSTANCE;

  private Map<String, RawSignatureStrategy> algorithms = new HashMap<>();

  public void register(String key, RawSignatureStrategy val) {
    algorithms.put(key, val);
  }

  public void deregister(String key) {
    algorithms.remove(key);
  }

  public RawSignatureStrategy get(String key) throws NoSuchStrategy {
    // find appropriate digital signature algorithm
    if (!has(key)) {
      throw new NoSuchStrategy(key);
    }

    return algorithms.get(key);
  }

  public boolean has(String key) {
    return algorithms.containsKey(key);
  }

  public void clear() {
    algorithms.clear();
  }

  public static class NoSuchStrategy extends Exception {

    public NoSuchStrategy(String key) {
      super(key + "signature suite is not implemented");
    }
  }

}
