/*
 * Created by murilo.mussatto on 01/03/2023
 */

package dev.mmussatto.expensetracker.controllers.exceptions;

import dev.mmussatto.expensetracker.services.exceptions.InvalidIdModificationException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException (ResourceNotFoundException exception, WebRequest request) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        responseBody.put("status", HttpStatus.NOT_FOUND.value());
        responseBody.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        responseBody.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        responseBody.put("message", exception.getMessage());


        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsException (ResourceAlreadyExistsException exception, WebRequest request) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        responseBody.put("status", HttpStatus.CONFLICT.value());
        responseBody.put("error", HttpStatus.CONFLICT.getReasonPhrase());
        responseBody.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        responseBody.put("message", exception.getMessage());
        responseBody.put("savedObjectPath", exception.getPathToObject());

        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(InvalidIdModificationException.class)
    public ResponseEntity<Object> handleInvalidIdModification(InvalidIdModificationException exception, WebRequest request) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        responseBody.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception, WebRequest request) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        responseBody.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        responseBody.put("messages", errors);

        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        responseBody.put("status", status.value());
        responseBody.put("error", HttpStatus.valueOf(status.value()).getReasonPhrase());
        responseBody.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        responseBody.put("messages", errors);

        return new ResponseEntity<>(responseBody, headers, status);
    }

}
