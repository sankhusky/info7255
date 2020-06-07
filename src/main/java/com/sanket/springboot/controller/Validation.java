package com.sanket.springboot.controller;

import org.everit.json.schema.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Validation {

    @ExceptionHandler({ValidationException.class})

    public ResponseEntity<String> handleNotFoundException(ValidationException e) {

        return error(HttpStatus.BAD_REQUEST, e);

    }
    
    private ResponseEntity<String> error(HttpStatus status, Exception e) {

        System.out.println("Exception : "+ e);

        return ResponseEntity.status(status).body(e.getMessage());

    }
	
}
