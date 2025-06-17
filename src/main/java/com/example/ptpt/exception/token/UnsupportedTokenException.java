package com.example.ptpt.exception.token;

import com.example.ptpt.enums.ApiResponseCode;
import com.example.ptpt.exception.AuthServiceException;

public class UnsupportedTokenException extends AuthServiceException {
    public UnsupportedTokenException() {
        super(ApiResponseCode.AUTH_TOKEN_UNSUPPORTED);
    }
}
