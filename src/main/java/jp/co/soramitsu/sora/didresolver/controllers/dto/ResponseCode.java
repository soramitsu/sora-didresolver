package jp.co.soramitsu.sora.didresolver.controllers.dto;

public enum ResponseCode {
  OK,
  ERROR,
  INCORRECT_QUERY_PARAMS,
  DID_IS_TOO_LONG,
  DID_DUPLICATE,
  DID_NOT_FOUND,
  INCORRECT_UPDATE_TIME,
  INVALID_PROOF,
  INVALID_PROOF_SIGNATURE,
  PUBLIC_KEY_VALUE_NOT_PRESENTED
}
