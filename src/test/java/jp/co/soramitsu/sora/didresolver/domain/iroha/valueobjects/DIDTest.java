package jp.co.soramitsu.sora.didresolver.domain.iroha.valueobjects;

import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.ED;
import static jp.co.soramitsu.sora.didresolver.commons.DIDTypeEnum.IROHA;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
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
  private static String edDid = "did:sora:ed:5LkqENiDNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn";
  private static String didIrohaWithDomain = "did:sora:iroha:bogdan@soramitsu.co.jp";
  @DataPoints
  public static List<String> invalidDids = new ArrayList<>();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @BeforeClass
  public static void setup() {
    String[] invalidDidsStringArr = {
        "plainly incorrect did",
        "did:sorar:uuid:caab4570-5f3f-4050-8d61-15306dea4bcf",
        "did:sora:iroha:someUser@",
        "did:sora:iroha:@domain",
        "did:sora:ed:5Lk$#@qENiDNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn",
        "did:sora:uuid:",
        "did:sora:iroha:someUser"
    };

    invalidDids = Arrays.stream(invalidDidsStringArr).collect(Collectors.toList());
  }

  @Test
  public void given_correctUUIDDid_assert_that_DID_created_successfully()
      throws DIDUnparseableException {
    val did = new DID(uuidDid);
    assertThat(did.getType(), equalTo(DIDTypeEnum.UUID));
    assertThat(did.getIdentifier(), equalTo("caab4570-5f3f-4050-8d61-15306dea4bcf"));
  }

  @Test
  public void given_correctEdDid_assert_that_DID_created_successfully()
      throws DIDUnparseableException {
    val did = new DID(edDid);
    assertThat(did.getType(), equalTo(ED));
    assertThat(did.getIdentifier(), equalTo("5LkqENiDNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn"));
  }

  @Test
  public void given_correctIrohaDid_assert_that_DID_created_successfully()
      throws DIDUnparseableException {
    val did = new DID(didIrohaWithDomain);
    assertThat(did.getType(), equalTo(IROHA));
    assertThat(did.getIdentifier(), equalTo("bogdan@soramitsu.co.jp"));
  }

  @Theory
  public void given_incorrect_dids_expect_exceptions(String did)
      throws DIDUnparseableException {
    expectedException.expect(DIDUnparseableException.class);
    new DID(did);
  }

  @Test
  public void given_null_expect_exception() throws DIDUnparseableException {
    expectedException.expect(NullPointerException.class);
    new DID(null);
  }

}
