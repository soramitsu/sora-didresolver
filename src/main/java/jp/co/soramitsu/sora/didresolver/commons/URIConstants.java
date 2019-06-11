package jp.co.soramitsu.sora.didresolver.commons;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class URIConstants {

  public static final String ID_PARAM = "/{did}";
  private static final String V1 = "/v1";
  public static final String PATH = V1 + "/did";
}
