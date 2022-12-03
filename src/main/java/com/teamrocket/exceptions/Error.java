package com.teamrocket.exceptions;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class Error {
    private String errorCode;
    private String request;
    private String requestType;
    private String message;
    private Date timestamp;
}