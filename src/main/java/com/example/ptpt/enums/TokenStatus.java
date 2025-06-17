package com.example.ptpt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenStatus {
    AUTHENTICATED,
    EXPIRED,
    INVALID;
}
