package com.example.ptpt.exception.token;


import com.example.ptpt.enums.ApiResponseCode;
import com.example.ptpt.exception.AuthServiceException;

public class InvalidSignatureTokenException extends AuthServiceException {
    public InvalidSignatureTokenException() {
        super(ApiResponseCode.AUTH_TOKEN_INVALID_SIGNATURE);
    }
}
