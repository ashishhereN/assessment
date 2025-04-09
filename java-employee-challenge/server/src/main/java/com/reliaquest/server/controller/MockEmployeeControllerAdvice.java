package com.reliaquest.server.controller;

import com.reliaquest.server.model.Response;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class MockEmployeeControllerAdvice {

	private static final Logger logger = LoggerFactory.getLogger(MockEmployeeControllerAdvice.class);
    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable ex) {
    	logger.error("Error handling web request.", ex);
        return ResponseEntity.internalServerError().body(Response.error(ex.getMessage()));
    }
}
