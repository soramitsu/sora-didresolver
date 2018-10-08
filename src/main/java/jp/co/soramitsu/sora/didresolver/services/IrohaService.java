package jp.co.soramitsu.sora.didresolver.services;

import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.exceptions.IrohaTransactionCommitmentException;

/**
 * This is the abstraction which hides implementation of communicating with Iroha network,
 * implementation is expected to work with either native bindings, or the iroha-pure-java library.
 * In general service expected to perform operations synchronously {@link
 * IrohaService#setAccountDetails(String, Object)}
 */
public interface IrohaService {

  /**
   * Returns details under given key
   *
   * @param key under which details returned
   * @return json string with details. Consider AccountDetail contains this data: '{ "key1": {
   * "nestedkey": "value" }, "key2": "othervalue" }'; When requested value under "key1" then {
   * "nestedkey": "value" } will be returned
   */
  Optional<String> getAccountDetails(String key);

  /**
   * Sets details under given key
   *
   * @param key under which details returned
   * @param value any Jackson-serializable object (can be list, or map, or any object serializable
   * with Jackson)
   */
  void setAccountDetails(String key, Object value) throws IrohaTransactionCommitmentException;
}
