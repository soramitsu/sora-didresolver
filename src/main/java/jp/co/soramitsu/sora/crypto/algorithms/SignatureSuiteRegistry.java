package jp.co.soramitsu.sora.crypto.algorithms;

import java.util.HashMap;
import java.util.Map;

public class SignatureSuiteRegistry {

  private static Map<String, RawSignatureStrategy> algorithms = new HashMap<>();

  private SignatureSuiteRegistry() {
  }

  public static void register(String key, RawSignatureStrategy val) {
    algorithms.put(key, val);
  }

  public static void deregister(String key) {
    algorithms.remove(key);
  }

  public static RawSignatureStrategy get(String key) {
    return algorithms.get(key);
  }

  public static boolean has(String key) {
    return algorithms.containsKey(key);
  }
}
