package com.example.ptpt.exception.token;


import com.example.ptpt.enums.ApiResponseCode;
import com.example.ptpt.exception.AuthServiceException;

public class BlacklistedTokenException extends AuthServiceException {
    public BlacklistedTokenException() {
        super(ApiResponseCode.AUTH_TOKEN_BLACKLISTED);
    }
}
