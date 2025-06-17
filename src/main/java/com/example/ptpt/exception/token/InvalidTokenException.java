package com.example.ptpt.exception.token;

import com.example.ptpt.enums.ApiResponseCode;
import com.example.ptpt.exception.AuthServiceException;

public class InvalidTokenException extends AuthServiceException {
    public InvalidTokenException() {
        super(ApiResponseCode.AUTH_TOKEN_INVALID);
    }

    public InvalidTokenException(String customMessage) {
        super(ApiResponseCode.AUTH_TOKEN_INVALID, customMessage);
    }
}