package com.ba.barcodereader.exception;

import com.ba.barcodereader.dto.ErrorResponseDTO;
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
    public final ResponseEntity<ErrorResponseDTO> handleBusinessRuleException(BusinessRuleException e, WebRequest request) {
        ErrorResponseDTO response = prepareResponseModel(e.getMessage(), request);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SystemException.class)
    public final ResponseEntity<ErrorResponseDTO> handleSystemException(SystemException e, WebRequest request) {
        ErrorResponseDTO response = prepareResponseModel(e.getMessage(), request);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponseDTO> handlerException(Exception e, WebRequest request) {
        ErrorResponseDTO response = prepareResponseModel(e.getMessage(), request);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponseDTO prepareResponseModel(String message, WebRequest request) {
        return new ErrorResponseDTO(new Date(), message, request.getDescription(false));
    }
}
