package ch.admin.bag.covidcertificate.gateway.web.controller;

import ch.admin.bag.covidcertificate.gateway.error.RestError;
import ch.admin.bag.covidcertificate.gateway.service.InvalidBearerTokenException;
import ch.admin.bag.covidcertificate.gateway.service.dto.CreateCertificateException;
import ch.admin.bag.covidcertificate.gateway.service.dto.ReadValueSetsException;
import ch.admin.bag.covidcertificate.gateway.service.dto.RevokeCertificateException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice()
@Slf4j
public class ResponseStatusExceptionHandler {

    @ExceptionHandler(value = {CreateCertificateException.class})
    protected ResponseEntity<RestError> createCertificateConflict(CreateCertificateException ex) {
        return handleError(ex.getError());
    }

    @ExceptionHandler(value = {RevokeCertificateException.class})
    protected ResponseEntity<RestError> createCertificateConflict(RevokeCertificateException ex) {
        return handleError(ex.getError());
    }

    @ExceptionHandler(value = {InvalidBearerTokenException.class})
    protected ResponseEntity<RestError> invalidBearer(InvalidBearerTokenException ex) {
        return handleError(ex.getError());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    protected ResponseEntity<RestError> notReadableRequestPayload(HttpMessageNotReadableException ex) {
        RestError error;
        try {
            var rootException = (InvalidFormatException) ex.getCause();
            assert rootException != null;
            var errorMessage = "Unable to parse " + rootException.getValue() + " to " + rootException.getTargetType();
            log.warn("HttpMessage with invalid format received: ", rootException);
            error = new RestError(HttpStatus.BAD_REQUEST.value(), errorMessage, HttpStatus.BAD_REQUEST);
        } catch (ClassCastException | AssertionError processingException) {
            log.warn("HttpMessage is not readable: ", ex);
            error = new RestError(HttpStatus.BAD_REQUEST.value(), "Http message not readable", HttpStatus.BAD_REQUEST);
        }
        return handleError(error);
    }

    @ExceptionHandler(value = {ReadValueSetsException.class})
    protected ResponseEntity<RestError> handleReadValueSetsException(ReadValueSetsException ex) {
        return handleError(ex.getError());
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error("Exception during processing", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<RestError> handleError(RestError restError) {
        log.warn("Error {}", restError);
        return new ResponseEntity<>(restError, restError.getHttpStatus());
    }
}
