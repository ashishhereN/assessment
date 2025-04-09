package com.reliaquest.api.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class EmployeeControllerAdvice {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeControllerAdvice.class);
    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable ex) {
    	logger.error("Error handling web request.", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
