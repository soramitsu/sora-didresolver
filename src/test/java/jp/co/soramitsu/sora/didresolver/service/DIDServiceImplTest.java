package jp.co.soramitsu.sora.didresolver.service;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static jp.co.soramitsu.sora.sdk.did.model.dto.DID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.when;
import static org.mockito.junit.MockitoJUnit.rule;

import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jp.co.soramitsu.sora.didresolver.domain.repositories.AccountRepository;
import jp.co.soramitsu.sora.didresolver.exceptions.UnparseableException;
import jp.co.soramitsu.sora.didresolver.services.StorageService;
import jp.co.soramitsu.sora.didresolver.services.impl.DIDServiceViaRepository;
import jp.co.soramitsu.sora.sdk.did.model.dto.DDO;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoRule;

@RunWith(Theories.class)
public class DIDServiceImplTest {

  @DataPoints
  public static List<String> incorrectDids = new ArrayList<>();
  @Rule
  public ExpectedException expectedException = none();
  @Rule
  public MockitoRule mockitoRule = rule();
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
    ).lines().collect(toList());

    storageService = new DIDServiceViaRepository(accountRepository);
  }

  @Test
  public void givenCorrectIrohaIdWhenCalledReadAssertAccountEntityReturned()
      throws UnparseableException {

    val did = "did:sora:soraUser8";
    val ddo = new DDO();
    ddo.setId(randomUUID());
    ddo.setCreated(now().truncatedTo(SECONDS));
    val ddoString = JacksonUtil.toString(ddo);

    when(accountRepository.findDDOByDid(did)).thenReturn(Optional.of(ddoString));

    val result = storageService.findDDObyDID(did);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), equalTo(ddo));
  }

}
