package jp.co.soramitsu.sora.service;

import static jp.co.soramitsu.sora.util.DataProvider.ID_BASE;
import static jp.co.soramitsu.sora.util.DataProvider.KEYS_COUNT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import jp.co.soramitsu.sora.didresolver.services.VerifyService;
import jp.co.soramitsu.sora.didresolver.services.impl.VerifyServiceImpl;
import jp.co.soramitsu.sora.sdk.did.model.dto.Authentication;
import jp.co.soramitsu.sora.sdk.did.model.dto.DID;
import jp.co.soramitsu.sora.sdk.did.model.dto.Proof;
import jp.co.soramitsu.sora.sdk.did.model.dto.PublicKey;
import jp.co.soramitsu.sora.sdk.did.parser.generated.ParserException;
import jp.co.soramitsu.sora.util.DataProvider;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ValidateServiceImplTest {

  @Autowired
  private VerifyService verifyService;

  private DataProvider dataProvider = new DataProvider();

  @TestConfiguration
  static class ValidateServiceImplTestContextConfiguration {

    @Bean
    public VerifyService verifyService() {
      return new VerifyServiceImpl();
    }
  }

  @Test
  public void testSuccessIsProofCreatorInAuth() throws ParserException {
    DID proofCreator = dataProvider.getProofForTest().getOptions().getCreator();
    assertTrue(verifyService.isCreatorInAuth(proofCreator,
        dataProvider.getAuthenticationForTest()));
  }

  @Test
  public void testFailedIsProofCreatorInAuth() throws ParserException {
    Proof proof = dataProvider.getProofForTest();
    List<Authentication> authentications = dataProvider.getAuthenticationForTest();
    proof.getOptions().setCreator(DID.parse(StringUtils.replaceChars(proof.getOptions().getCreator().toString(), '5', '8')));
    assertFalse(verifyService.isCreatorInAuth(proof.getOptions().getCreator(), authentications));
    proof.getOptions().setCreator(DID.parse(ID_BASE + KEYS_COUNT + 2));
    assertFalse(verifyService.isCreatorInAuth(proof.getOptions().getCreator(), authentications));
  }

  @Test
  public void testSuccessIsProofInPublicKeys() {
    assertTrue(verifyService.isCreatorInPublicKeys(dataProvider.getProofForTest().getOptions().getCreator(),
        dataProvider.getPublicKeysForTest()));
  }

  @Test
  public void testFailedIsProofInPublicKeys() throws ParserException {
    Proof proof = dataProvider.getProofForTest();
    List<PublicKey> publicKeys = dataProvider.getPublicKeysForTest();
    proof.getOptions().setCreator(DID.parse(StringUtils.replaceChars(proof.getOptions().getCreator().toString(), '5', '8')));
    assertFalse(verifyService.isCreatorInPublicKeys(proof.getOptions().getCreator(), publicKeys));
    proof.getOptions().setCreator(DID.parse(ID_BASE + KEYS_COUNT + 2));
    assertFalse(verifyService.isCreatorInPublicKeys(proof.getOptions().getCreator(), publicKeys));
  }
}
