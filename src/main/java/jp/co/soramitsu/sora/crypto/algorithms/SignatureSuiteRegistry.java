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

  public static RawSignatureStrategy get(String key) throws InvalidAlgorithmException {
    // find appropriate digital signature algorithm
    if (!SignatureSuiteRegistry.has(key)) {
      throw new InvalidAlgorithmException(key + " signature suite is not implemented");
    }

    return algorithms.get(key);
  }

  public static boolean has(String key) {
    return algorithms.containsKey(key);
  }

  public static void clear() {
    algorithms.clear();
  }

  public static class InvalidAlgorithmException extends Exception {

    public InvalidAlgorithmException(String message) {
      super(message);
    }
  }

}
