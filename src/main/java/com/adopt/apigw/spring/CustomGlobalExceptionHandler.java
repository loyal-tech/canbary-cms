package com.adopt.apigw.spring;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.adopt.apigw.utils.APIConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//@org.springframework.core.annotation.Order(Ordered.HIGHEST_PRECEDENCE)
//@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
        body.put("status", status.value());

        //Get all errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        System.out.println("ERROR: " + ex.getBindingResult().getAllErrors());
//        Map<String, String> errors = new HashMap<String, String>();
//	    ex.getBindingResult().getAllErrors().forEach((error) -> {
//        String fieldName = ((FieldError) error).getField();
//        String errorMessage = error.getDefaultMessage();
//        errors.put(fieldName, errorMessage);
//    });


        body.put(APIConstants.ERROR_TAG, errors);
        return new ResponseEntity<>(body, headers, status);
    }

}