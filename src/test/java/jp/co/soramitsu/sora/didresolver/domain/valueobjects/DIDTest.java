package jp.co.soramitsu.sora.didresolver.domain.valueobjects;

import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.IROHA;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDUnparseableException;
import lombok.val;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class DIDTest {

  private static String uuidDid = "did:sora:uuid:caab4570-5f3f-4050-8d61-15306dea4bcf";
  private static String didIrohaWithDomain = "did:sora:iroha:bogdan@soramitsu.co.jp";
  @DataPoints
  public static List<String> invalidDids = new ArrayList<>();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @BeforeClass
  public static void setUp() {
    invalidDids =
        new BufferedReader(
            new InputStreamReader(
                DIDTest.class.getClassLoader()
                    .getResourceAsStream("incorrectDids.txt")
            )
        ).lines().collect(Collectors.toList());
  }

  @Test
  public void givenCorrectUUIDDidAssertThatDIDCreatedSuccessfully()
      throws DIDUnparseableException {
    val did = new DID(uuidDid);
    assertThat(did.getType(), equalTo(DIDTypeEnum.UUID));
    assertThat(did.getIdentifier(), equalTo("caab4570-5f3f-4050-8d61-15306dea4bcf"));
  }

  @Test
  public void givenCorrectIrohaDidAssertThatDIDCreatedSuccessfully()
      throws DIDUnparseableException {
    val did = new DID(didIrohaWithDomain);
    assertThat(did.getType(), equalTo(IROHA));
    assertThat(did.getIdentifier(), equalTo("bogdan@soramitsu.co.jp"));
  }

  @Theory
  public void givenIncorrectDidsExpectExceptions(String did)
      throws DIDUnparseableException {
    expectedException.expect(DIDUnparseableException.class);
    new DID(did);
  }

  @Test
  public void givenNullExpectException() throws DIDUnparseableException {
    expectedException.expect(IllegalArgumentException.class);
    new DID(null);
  }

}
