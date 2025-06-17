package com.example.ptpt.exception;

import com.example.ptpt.enums.ApiResponseCode;
import lombok.Getter;

@Getter
public class AuthServiceException extends RuntimeException {

    final ApiResponseCode responseCode;

    public AuthServiceException(ApiResponseCode responseCode) {
        super(responseCode.getDefaultMessage());
        this.responseCode = responseCode;
    }

    public AuthServiceException(ApiResponseCode responseCode, String customMessage) {
        super(customMessage);
        this.responseCode = responseCode;
    }
}
