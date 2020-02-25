package com.ba.barcodereader.exception;

import com.ba.barcodereader.model.ErrorResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestController
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public final ResponseEntity<ErrorResponseModel> handleBusinessRuleException(BusinessRuleException e, WebRequest request) {
        ErrorResponseModel response = prepareResponseModel(e.getMessage(), request);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SystemException.class)
    public final ResponseEntity<ErrorResponseModel> handleSystemException(SystemException e, WebRequest request) {
        ErrorResponseModel response = prepareResponseModel(e.getMessage(), request);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponseModel> handlerException(Exception e, WebRequest request) {
        ErrorResponseModel response = prepareResponseModel(e.getMessage(), request);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponseModel prepareResponseModel(String message, WebRequest request) {
        return new ErrorResponseModel(new Date(), message, request.getDescription(false));
    }
}