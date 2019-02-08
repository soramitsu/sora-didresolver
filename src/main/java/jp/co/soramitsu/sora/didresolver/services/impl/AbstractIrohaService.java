package jp.co.soramitsu.sora.didresolver.services.impl;

import static com.fasterxml.jackson.core.util.BufferRecyclers.quoteAsJsonText;
import static com.jayway.jsonpath.Configuration.defaultConfiguration;
import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;
import static java.lang.String.valueOf;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.isEmpty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import io.reactivex.Observer;
import iroha.protocol.Endpoint.ToriiResponse;
import iroha.protocol.Queries.Query;
import iroha.protocol.TransactionOuterClass;
import java.security.KeyPair;
import java.util.Optional;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.CryptoException;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.TransactionStatusObserver;
import jp.co.soramitsu.sora.didresolver.exceptions.IrohaTransactionCommitmentException;
import jp.co.soramitsu.sora.didresolver.services.IrohaService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;

@RequiredArgsConstructor
public abstract class AbstractIrohaService implements IrohaService {

  private static final String FORMAT = "$.%s.%s";
  /**
   * Supresses exception when key in json is not present
   */
  private final Configuration suppressingExceptionConfig =
      defaultConfiguration().addOptions(SUPPRESS_EXCEPTIONS);

  private final Logger log = getLogger(getClass());

  private final IrohaAPI api;

  protected abstract KeyPair keyPair();

  protected abstract ObjectMapper objectMapper();

  protected abstract String irohaAccount();

  @Override
  public Optional<String> getAccountDetails(String detailKey) {
    String key = getNormalizeDetailKey(detailKey);
    val account = irohaAccount();
    try {
      log.debug(
          "getting account details by key {} for Iroha account {} at {}",
          key,
          account,
          now());
      val queryResponse = api.query(getAccountDetailsQuery(key));
      String response = queryResponse.getAccountDetailResponse().getDetail();
      log.debug("received the Iroha account detail {} for key {}", response, key);
      if (isEmpty(response)) {
        return Optional.empty();
      }
      return ofNullable(
          using(suppressingExceptionConfig)
              .parse(response)
              .read(pathForDetailWithKey(key))
      );
    } catch (CryptoException e) {
      log.warn("Can't sign query: {}", e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void setAccountDetails(String detailKey, Object value) {
    String key = getNormalizeDetailKey(detailKey);
    setAccountDetailsAsync(key, value);
  }

  private Observer<? super ToriiResponse> getObserver(String txKey) {
    return TransactionStatusObserver.builder()
        .onTransactionFailed(tx ->
            log.error("transaction {} failed with msg: {}", tx.getTxHash(), tx.getErrOrCmdName()))
        .onError(e -> {
          log.error("Transaction failed with exception", e);
          throw new IrohaTransactionCommitmentException(txKey, e);
        })
        .onTransactionCommitted(tx -> log.debug("tx {} is committed", tx.getTxHash()))
        .onRejected(
            toriiResponse -> log
                .warn("tx {} is rejected with reason code {}", toriiResponse.getTxHash(),
                    toriiResponse.getFailedCmdIndex()))
        .build();
  }

  private void setAccountDetailsAsync(String key, Object value) {
    String v;
    try {
      v = valueOf(quoteAsJsonText(objectMapper().writeValueAsString(value)));
    } catch (JsonProcessingException e) {
      log.error("Problem with processing json {} for object with key {}", value, key);
      throw new IrohaTransactionCommitmentException(key, e);
    }

    val tx = setAccountDetailsTransaction(key, v);

    log.debug("send transaction {} to iroha at {}", tx, api.getUri());

    api.transaction(tx).blockingSubscribe(getObserver(key));
  }

  private TransactionOuterClass.Transaction setAccountDetailsTransaction(String key,
      String value) {
    return jp.co.soramitsu.iroha.java.Transaction.builder(irohaAccount())
        .setAccountDetail(
            irohaAccount(),
            key,
            value
        )
        .sign(keyPair())
        .build();
  }

  private Query getAccountDetailsQuery(String key) {
    return jp.co.soramitsu.iroha.java.Query.builder(irohaAccount(), now(), 1)
        .getAccountDetail(irohaAccount(), null, key)
        .buildSigned(keyPair());
  }

  private String getNormalizeDetailKey(String detailKey) {
    return detailKey.replaceAll("[\\s-:.@]", "_");
  }

  private String pathForDetailWithKey(String key) {
    return String.format(FORMAT, irohaAccount(), key);
  }
}
