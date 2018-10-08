package jp.co.soramitsu.sora.didresolver.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jp.co.soramitsu.sora.didresolver.IrohaIntegrationTest;
import jp.co.soramitsu.sora.didresolver.services.IrohaService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class IrohaServiceTests extends IrohaIntegrationTest {

  public static final String ID = "someid";
  @Autowired
  private IrohaService irohaService;
  @Autowired
  private ObjectMapper mapper;

  @Test
  public void canSetAndGetArray() throws IOException {
    List<SomeClass> someClasses =
        IntStream.range(0, 5).mapToObj(i -> new SomeClass(ID, i)).collect(Collectors.toList());
    irohaService.setAccountDetails(ID, someClasses);
    Optional<String> val = irohaService.getAccountDetails(ID);

    assertThat(
        mapper.readValue(val.get(), new TypeReference<List<SomeClass>>() {
        }), is(someClasses));
  }

  @Test
  public void canSetAndGetObject() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    SomeClass payloadObject = new SomeClass("someid", 1);
    irohaService.setAccountDetails(ID, payloadObject);
    Optional<String> val = irohaService.getAccountDetails(ID);

    assertThat(mapper.readValue(val.get(), SomeClass.class), is(payloadObject));
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class SomeClass {

    String id;
    int counter;
  }
}
