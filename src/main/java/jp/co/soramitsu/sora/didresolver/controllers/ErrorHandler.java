package jp.co.soramitsu.sora.didresolver.controllers;

import static jp.co.soramitsu.sora.didresolver.controllers.dto.ErrorRs.BusinessErrors.DID_DUPLICATE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import jp.co.soramitsu.sora.didresolver.controllers.dto.ErrorRs;
import jp.co.soramitsu.sora.didresolver.exceptions.DIDDuplicateException;
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

  private static final String ERROR_FORMAT = "{ \"error\" : \"%s\"}\n";

  @ExceptionHandler(value = {DIDDuplicateException.class})
  protected ResponseEntity<Object> handleExceptions(
      RuntimeException ex, WebRequest request) {

    ErrorRs errorRs = null;

    if (ex instanceof DIDDuplicateException) {
      errorRs = new ErrorRs(DID_DUPLICATE.name());
    }

    return handleExceptionInternal(ex, errorRs, new HttpHeaders(), OK, request);
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    StringBuilder strBuilder = new StringBuilder();
    for (ConstraintViolation<?> violation : violations) {
      strBuilder.append(String.format(ERROR_FORMAT, violation.getMessage()));
    }
    log.error(e.toString(), e);
    return new ResponseEntity<>(strBuilder.toString(), new HttpHeaders(), BAD_REQUEST);
  }
}
