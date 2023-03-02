/*
 * Created by murilo.mussatto on 01/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler({ResourceNotFoundException.class})
//    public ResponseEntity<Object> handleNotFoundException (Exception exception, WebRequest request) {
//        return new ResponseEntity<>("Resource Not Found!", new HttpHeaders(), HttpStatus.NOT_FOUND);
//    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<CustomErrorResponse> handleNotFoundException (Exception exception, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        errorResponse.setPath(request.getContextPath());

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler({ResourceAlreadyExistsException.class})
//    public ResponseEntity<Object> handleAlreadyExistsException (Exception exception, WebRequest request) {
//        return new ResponseEntity<>("Resource Already Exists!\n" + exception.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
//    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsException (Exception exception, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setMessage("Resource already exists!");
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError(HttpStatus.CONFLICT.getReasonPhrase());
        errorResponse.setPath(exception.getMessage());

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.CONFLICT);
    }
}
