package com.example.ptpt.exception.token;


import com.example.ptpt.enums.ApiResponseCode;
import com.example.ptpt.exception.AuthServiceException;

public class ExpiredTokenException extends AuthServiceException {
    public ExpiredTokenException() {
        super(ApiResponseCode.AUTH_TOKEN_EXPIRED);
    }

    public ExpiredTokenException(String customMessage) {
        super(ApiResponseCode.AUTH_TOKEN_EXPIRED, customMessage);
    }
}
