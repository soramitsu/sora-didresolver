package jp.co.soramitsu.sora.crypto.algorithms;

import java.util.HashMap;
import java.util.Map;

public class SignatureSuiteRegistry {

  private SignatureSuiteRegistry(){}

  private static Map<String, RawSignatureStrategy> algorithms = new HashMap<>();

  public static void register(String key, RawSignatureStrategy val) {
    algorithms.put(key, val);
  }

  public static RawSignatureStrategy get(String key) {
    return algorithms.get(key);
  }

  public static boolean has(String key) {
    return algorithms.containsKey(key);
  }
}