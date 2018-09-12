package jp.co.soramitsu.sora.didresolver.controllers;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ErrorRs.BusinessErrors.DID_DUPLICATE;
import static jp.co.soramitsu.sora.didresolver.controllers.dto.ErrorRs.BusinessErrors.UNKNOWN;
import static org.springframework.http.HttpStatus.OK;

import jp.co.soramitsu.sora.didresolver.controllers.dto.ErrorRs;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {DIDDuplicateException.class})
  protected ResponseEntity<Object> handleExceptions(
      RuntimeException ex, WebRequest request) {

    ErrorRs errorRs;

    if (ex instanceof DIDDuplicateException) {
      errorRs = new ErrorRs(DID_DUPLICATE.name());
    } else {
      errorRs = new ErrorRs(UNKNOWN.name());
    }

    return handleExceptionInternal(ex, errorRs, new HttpHeaders(), OK, request);
  }
}
