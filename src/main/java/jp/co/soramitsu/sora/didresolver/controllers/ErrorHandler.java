package jp.co.soramitsu.sora.didresolver.controllers;

import static java.util.Optional.ofNullable;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.ERROR;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode.INCORRECT_QUERY_PARAMS;
import static org.springframework.http.HttpStatus.OK;

import javax.validation.ConstraintViolationException;
import jp.co.soramitsu.sora.didresolver.controllers.dto.GenericResponse;
import jp.co.soramitsu.sora.didresolver.controllers.dto.GenericResponse.Status;
import jp.co.soramitsu.sora.didresolver.controllers.dto.ResponseCode;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDResolverException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e,
      WebRequest request) {
    return handleException(ERROR, e, request);
  }

  @ExceptionHandler(value = {DIDResolverException.class})
  protected ResponseEntity<Object> handleDIDResolverExceptions(
      DIDResolverException ex, WebRequest request) {
    return handleException(ofNullable(ex.getResponseCode()).orElse(ERROR), ex, request);
  }

  @ExceptionHandler(value = {IllegalArgumentException.class})
  protected ResponseEntity<Object> handleIllegalArgumentExceptions(
      IllegalArgumentException ex, WebRequest request) {
    return handleException(INCORRECT_QUERY_PARAMS, ex, request);
  }

  private ResponseEntity<Object> handleException(ResponseCode responseCode, Exception ex,
      WebRequest request) {
    log.warn(ex.getMessage(), ex);
    return handleExceptionInternal(ex,
        new GenericResponse(
            new Status(responseCode, ex.getMessage())),
        new HttpHeaders(), OK, request);
  }
}
