package com.teamrocket.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.NoSuchElementException;

@ControllerAdvice
@RestControllerAdvice
public class ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Error> noSuchElementException(
            NoSuchElementException noSuchElementException,
            HttpServletRequest request) {

        LOGGER.error("validation exception : {} for request: {}",
                noSuchElementException.getLocalizedMessage(), request.getRequestURI());

        return new ResponseEntity<>(
                Error.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.toString())
                        .request(request.getRequestURI())
                        .requestType(request.getMethod())
                        .message("Request is not valid")
                        .timestamp(new Date())
                        .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({KafkaException.class})
    public ResponseEntity<Error> kafkaException(
            KafkaException kafkaException,
            HttpServletRequest request) {

        LOGGER.error("validation exception : {} for request: {}",
                kafkaException.getLocalizedMessage(), request.getRequestURI());

        return new ResponseEntity<>(
                Error.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.toString())
                        .request(request.getRequestURI())
                        .requestType(request.getMethod())
                        .message("Could not process request")
                        .timestamp(new Date())
                        .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Error> genericException(
            Exception exception,
            HttpServletRequest request) {

        LOGGER.error("exception : {} for request: {}", exception.getLocalizedMessage(), request.getRequestURI());

        return new ResponseEntity<>(
                Error.builder()
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .request(request.getRequestURI())
                        .requestType(request.getMethod())
                        .message("Could not process request")
                        .timestamp(new Date())
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
