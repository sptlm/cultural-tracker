package com.culturalnavigator.exception;

import java.time.Instant;

public record ApiErrorResponse(
        String status,
        String message,
        Instant timestamp
) {
}
