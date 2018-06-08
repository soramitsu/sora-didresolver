package jp.co.soramitsu.sora.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.co.soramitsu.sora.didresolver.domain.iroha.repositories.AccountRepository;
import jp.co.soramitsu.sora.didresolver.domain.iroha.valueobjects.DID;
import jp.co.soramitsu.sora.didresolver.dto.DDO;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.impl.DIDServiceViaRepository;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@RunWith(Theories.class)
public class DIDServiceImplTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @DataPoints
  public static List<String> incorrectDids = new ArrayList<>();

  @Mock
  private AccountRepository accountRepository;

  private StorageService storageService;

  @Before
  public void setUp() {
    String[] invalidDidsStringArr = {
        "plainly incorrect did",
        "did:sorar:uuid:caab4570-5f3f-4050-8d61-15306dea4bcf",
        "did:sora:iroha:someUser@",
        "did:sora:iroha:@domain",
        "did:sora:ed:5LkqENi#OI!2_)9DNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn",
        "did:sora:uuid:",
        "did:sora:iroha:someUser",
        "did:sora:iroha:5LkqENiDNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn"
    };

    incorrectDids = Arrays.stream(invalidDidsStringArr).collect(Collectors.toList());

    storageService = new DIDServiceViaRepository(accountRepository);
  }

  @Theory
  public void given_incorrectDids_when_called_read_assert_exception_thrown(String did)
      throws UnparseableException {
    expectedException.expect(UnparseableException.class);
    val didObject = new DID(did);

    storageService.read(didObject.getDidString());
  }

  @Test
  public void given_correct_iroha_id_when_called_read_assert_account_entity_returned()
      throws UnparseableException {
    val did = "did:sora:iroha:vasya@home.ru";
    val ddo = new DDO();
    ddo.setId(did);
    ddo.setCreated(Timestamp.from(Instant.now()));
    val ddoString = JacksonUtil.toString(ddo);

    when(accountRepository.findDDOByDid(did)).thenReturn(Optional.of(ddoString));

    val result = storageService.read(did);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), equalTo(ddo));
  }

}
