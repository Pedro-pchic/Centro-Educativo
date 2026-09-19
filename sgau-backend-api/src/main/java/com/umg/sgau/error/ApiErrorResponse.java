package com.umg.sgau.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {

    public ApiErrorResponse {
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            fieldErrors = Collections.unmodifiableMap(
                    new LinkedHashMap<>(fieldErrors));
        } else {
            fieldErrors = null;
        }
    }
}
