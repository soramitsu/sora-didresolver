package jp.co.soramitsu.sora.didresolver.controllers.dto;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.OK;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class SuccessfulResponse extends GenericResponse {

  private static final Status status = new Status(OK, "Success");

  public SuccessfulResponse() {
    super(status);
  }
}
