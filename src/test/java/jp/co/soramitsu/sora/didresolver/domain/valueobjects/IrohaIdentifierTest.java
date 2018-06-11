package jp.co.soramitsu.sora.didresolver.domain.valueobjects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

import jp.co.soramitsu.sora.didresolver.exceptions.IrohaIdentifierUnparseableException;
import lombok.val;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class IrohaIdentifierTest {

  private static String uuid = "caab4570-5f3f-4050-8d61-15306dea4bcf";
  private static String ed = "5LkqENiDNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn";
  private static String accountWithoutDomain = "someUser";
  private static String accountWithDomain = "bogdan@soramitsu.co.jp";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void givenCorrectUuidIdentifierAssertIrohaIdentifierThrows()
      throws IrohaIdentifierUnparseableException {
    expectedException.expect(IrohaIdentifierUnparseableException.class);
    new IrohaIdentifier(uuid);
  }

  @Test
  public void givenCorrectEdIdentifierAssertIrohaIdentifierThrows()
      throws IrohaIdentifierUnparseableException {
    expectedException.expect(IrohaIdentifierUnparseableException.class);
    new IrohaIdentifier(ed);
  }

  @Test
  public void givenCorrectIrohaIdentifierWithDomainAssertIrohaIdentifierCreated()
      throws IrohaIdentifierUnparseableException {
    val identifier = new IrohaIdentifier(accountWithDomain);

    assertThat(identifier, hasProperty("identifier", equalTo("bogdan@soramitsu.co.jp")));
    assertThat(identifier, hasProperty("accountId", equalTo("bogdan")));
    assertThat(identifier, hasProperty("domainId", equalTo("soramitsu.co.jp")));
  }

  @Test
  public void givenCorrectIrohaIdentifierWithoutDomainAssertIrohaIdentifierThrows()
      throws IrohaIdentifierUnparseableException {
    expectedException.expect(IrohaIdentifierUnparseableException.class);
    new IrohaIdentifier(accountWithoutDomain);
  }

}