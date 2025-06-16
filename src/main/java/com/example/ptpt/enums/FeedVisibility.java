package com.example.ptpt.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FeedVisibility {
    PRIVATE("비공개"),
    PUBLIC("공개");

    private final String krValue;

    FeedVisibility(String krValue) {
        this.krValue = krValue;
    }

    @JsonValue
    public String getKrValue() {
        return krValue;
    }
}
