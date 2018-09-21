package jp.co.soramitsu.sora.didresolver.services.impl;

import static com.fasterxml.jackson.core.util.BufferRecyclers.quoteAsJsonText;
import static com.google.protobuf.ByteString.copyFrom;
import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;
import static java.lang.String.valueOf;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.springframework.util.StringUtils.isEmpty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;
import com.jayway.jsonpath.Configuration;
import io.grpc.ManagedChannel;
import iroha.protocol.CommandServiceGrpc;
import iroha.protocol.CommandServiceGrpc.CommandServiceBlockingStub;
import iroha.protocol.Commands.Command;
import iroha.protocol.Commands.SetAccountDetail;
import iroha.protocol.Endpoint.ToriiResponse;
import iroha.protocol.Endpoint.TxStatus;
import iroha.protocol.Endpoint.TxStatusRequest;
import iroha.protocol.QryResponses.QueryResponse;
import iroha.protocol.Queries.Query;
import iroha.protocol.QueryServiceGrpc;
import iroha.protocol.QueryServiceGrpc.QueryServiceBlockingStub;
import iroha.protocol.TransactionOuterClass.Transaction;
import iroha.protocol.TransactionOuterClass.Transaction.Payload.ReducedPayload;
import java.security.KeyPair;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.CryptoException;
import jp.co.soramitsu.iroha.java.Signer;
import jp.co.soramitsu.sora.didresolver.exceptions.IrohaTransactionCommitmentException;
import jp.co.soramitsu.sora.didresolver.services.IrohaService;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.spongycastle.jcajce.provider.digest.SHA3.Digest256;

public abstract class AbstractIrohaService implements IrohaService {

  private static final String TX_HASH = "TxHash";
  private static final String FORMAT = "$.%s.%s";
  /** Supresses exception when key in json is not present */
  private final Configuration suppressingExceptionConfig =
      Configuration.defaultConfiguration().addOptions(SUPPRESS_EXCEPTIONS);

  private Digest256 sha3;
  private Signer signer;

  private Logger log;

  protected abstract KeyPair keyPair();

  protected abstract ObjectMapper objectMapper();

  protected abstract String irohaAccount();

  protected abstract ManagedChannel irohaChannel();

  public AbstractIrohaService(Logger log) {
    this.log = log;
    sha3 = new Digest256();
  }

  @PostConstruct
  private void initialize() {
    signer = new Signer(keyPair());
  }

  @Override
  public Optional<String> getAccountDetails(String detailKey) {
    String key = getNormalizeDetailKey(detailKey);
    try {
      Query uquery = getAccountDetailsQuery(key);
      QueryServiceBlockingStub queryStub = QueryServiceGrpc.newBlockingStub(irohaChannel());
      QueryResponse queryResponse = queryStub.find(uquery);
      String response = queryResponse.getAccountDetailResponse().getDetail();
      if (isEmpty(response)) {
        return Optional.empty();
      }
      return ofNullable(
          using(suppressingExceptionConfig).parse(response).read(pathForDetailWithKey(key)));
    } catch (CryptoException e) {
      log.warn("Can't sign query: {}", e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  private String pathForDetailWithKey(String key) {
    return String.format(FORMAT, irohaAccount(), key);
  }

  private String toHex(byte[] byteArray) {
    return encodeHexString(sha3.digest(byteArray));
  }

  private byte[] toBytes(GeneratedMessageV3.Builder msgLite) {
    return msgLite.buildPartial().toByteArray();
  }

  private ByteString txHash(Transaction tx) {
    return copyFrom(sha3.digest(toBytes(tx.getPayload().toBuilder())));
  }

  @Override
  public void setAccountDetails(String detailKey, Object value)
      throws IrohaTransactionCommitmentException {
    String key = getNormalizeDetailKey(detailKey);
    try {
      /*
       * BufferRecyclers#quoteAsJsonText is a workaround for bug in Iroha, when Iroha can't parse normal JSON string
       * TODO: remove when bug is fixed (tests will indicate that)
       * */
      val quotedPayload = valueOf(quoteAsJsonText(objectMapper().writeValueAsString(value)));
      val utx = setAccountDetailsUtx(key, quotedPayload);
      log.debug("Sending Tx to Iroha");
      val stub = CommandServiceGrpc.newBlockingStub(irohaChannel());
      val tx = signer.sign(utx, Instant.now(), irohaAccount());
      MDC.put(TX_HASH, toHex(txHash(tx).toByteArray()));
      stub.torii(tx);
      //      TODO: extract timeout to properties
      runAsync(() -> processTx(tx, stub)).get(10, SECONDS);
    } catch (JsonProcessingException e) {
      log.warn("Can't serialize value {}, reason: {}", value, e.getMessage());
      throw new IllegalArgumentException("Not serializable", e);
    } catch (CryptoException e) {
      log.warn("Failed to sign transaction: {}", e.getMessage());
    } catch (InterruptedException e) {
      log.error("Interrupted while waiting for tx status: {}", e.getMessage());
    } catch (ExecutionException e) {
      log.warn("Can't process tx {}", e.getMessage());
      throw new RuntimeException(e);
    } catch (TimeoutException e) {
      //      FIXME: decide what to do in this case
      log.error("Transaction taking too long, status is unknown");
    } finally {
      MDC.remove(TX_HASH);
    }
  }

  private void processTx(Transaction utx, CommandServiceBlockingStub stub)
      throws IrohaTransactionCommitmentException {
    boolean canBreakLoop = false;
    val txHash = txHash(utx);
    val request = TxStatusRequest.newBuilder().setTxHash(txHash).build();
    while (!canBreakLoop) {
      try {
        MILLISECONDS.sleep(500);
      } catch (InterruptedException e) {
        log.warn("Interrupted while checking tx status");
        break;
      }
      val status = txStatus(stub, request);
      switch (status) {
        case STATEFUL_VALIDATION_SUCCESS:
          log.info("Tx passed stateful validation");
          break;
        case STATELESS_VALIDATION_SUCCESS:
          log.info("Tx passed stateless validation");
          break;
        case COMMITTED:
          log.info("Transaction have been successfully committed");
          canBreakLoop = true;
          break;
        case MST_EXPIRED:
          log.warn("MST Tx expired");
          throw new IrohaTransactionCommitmentException(
              encodeHexString(txHash.toByteArray()), status);
        case NOT_RECEIVED:
          log.warn("Tx was not received");
          throw new IrohaTransactionCommitmentException(
              encodeHexString(txHash.toByteArray()), status);
        case UNRECOGNIZED:
          log.warn("Tx was not recognized");
          throw new IrohaTransactionCommitmentException(
              encodeHexString(txHash.toByteArray()), status);
        case STATEFUL_VALIDATION_FAILED:
          log.warn("Tx stateful validation failed");
          throw new IrohaTransactionCommitmentException(
              encodeHexString(txHash.toByteArray()), status);
        case STATELESS_VALIDATION_FAILED:
          log.warn("Tx stateless validation failed");
          throw new IrohaTransactionCommitmentException(
              encodeHexString(txHash.toByteArray()), status);
      }
    }
  }

  private TxStatus txStatus(CommandServiceBlockingStub stub, TxStatusRequest request) {
    ToriiResponse response = stub.status(request);
    return response.getTxStatus();
  }

  protected ReducedPayload.Builder setAccountDetailsUtx(String key, String value) {
    return ReducedPayload.newBuilder()
        .addCommands(
            Command.newBuilder()
                .setSetAccountDetail(
                    SetAccountDetail.newBuilder()
                        .setKey(key)
                        .setValue(value)
                        .setAccountId(irohaAccount())
                        .build())
                .build());
  }

  protected Query getAccountDetailsQuery(String key) throws CryptoException {
    return jp.co.soramitsu.iroha.java.Query.builder(irohaAccount(), now(), 1)
        .getAccountDetail(irohaAccount(), key)
        .buildSigned(keyPair());
  }

  private String getNormalizeDetailKey(String detailKey) {
    return detailKey.replaceAll("[ -:.@]", "_");
  }
}
