package jp.co.soramitsu.sora.didresolver.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import java.security.KeyPair;
import jp.co.soramitsu.sora.didresolver.config.properties.IrohaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IrohaServiceImpl extends AbstractIrohaService {

  private final KeyPair keyPair;
  private final ObjectMapper objectMapper;
  private final String irohaAccount;
  private final ManagedChannel irohaChannel;

  @Autowired
  public IrohaServiceImpl(ObjectMapper objectMapper,
      IrohaProperties irohaProperties, ManagedChannel irohaChannel) {
    super(log);
    this.objectMapper = objectMapper;
    this.keyPair = irohaProperties.getAccount().keyPair();
    this.irohaAccount = irohaProperties.getAccount().getName();
    this.irohaChannel = irohaChannel;
  }

  @Override
  protected KeyPair keyPair() {
    return keyPair;
  }

  @Override
  protected ObjectMapper objectMapper() {
    return objectMapper;
  }

  @Override
  protected String irohaAccount() {
    return irohaAccount;
  }

  @Override
  protected ManagedChannel irohaChannel() {
    return irohaChannel;
  }
}
