package com.culturalnavigator.dto;

import java.math.BigDecimal;
import java.util.List;

public record RouteSummaryDto(
        Long id,
        String title,
        String description,
        String authorUsername,
        Integer durationMinutes,
        BigDecimal budget,
        Boolean isPublic,
        Double distanceKm,
        List<EventCardDto> events
) {
}
