package jp.co.soramitsu.sora.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseValidatorTest {

  protected static DDO ddo;

  protected static Validator validator;

  private static ObjectMapper objectMapper = new ObjectMapper();

  @BeforeAll
  public static void setUpClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @BeforeEach
  public void setUp() throws IOException {
    Reader jsonReader = new BufferedReader(
        new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ddo.json")));
    ddo = objectMapper.readValue(jsonReader, DDO.class);
  }
}
