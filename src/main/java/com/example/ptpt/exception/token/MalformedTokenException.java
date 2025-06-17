package com.example.ptpt.exception.token;

import com.example.ptpt.enums.ApiResponseCode;
import com.example.ptpt.exception.AuthServiceException;

public class MalformedTokenException extends AuthServiceException {
    public MalformedTokenException() {
        super(ApiResponseCode.AUTH_TOKEN_MALFORMED);
    }

    public MalformedTokenException(String customMessage) {
        super(ApiResponseCode.AUTH_TOKEN_MALFORMED, customMessage);
    }
}
