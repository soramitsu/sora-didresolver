package jp.co.soramitsu.sora.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.co.soramitsu.sora.didresolver.domain.repositories.AccountRepository;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.impl.DIDServiceViaRepository;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;
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
    incorrectDids = new BufferedReader(
        new InputStreamReader(
            getClass()
                .getClassLoader()
                .getResourceAsStream("incorrectDids.txt")
        )
    ).lines().collect(Collectors.toList());

    storageService = new DIDServiceViaRepository(accountRepository);
  }

  @Test
  public void givenCorrectIrohaIdWhenCalledReadAssertAccountEntityReturned()
      throws UnparseableException, ParserException {

    val did = "did:sora:soraUser8";
    val ddo = new DDO();
    ddo.setId(DID.randomUUID());
    ddo.setCreated(Instant.now().truncatedTo(ChronoUnit.SECONDS));
    val ddoString = JacksonUtil.toString(ddo);

    when(accountRepository.findDDOByDid(did)).thenReturn(Optional.of(ddoString));

    val result = storageService.findDDObyDID(did);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), equalTo(ddo));
  }

}
