package com.culturalnavigator.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventCardDto(
        Long id,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String startLabel,
        String timeLabel,
        BigDecimal price,
        Long venueId,
        String venueName,
        String districtName,
        String imageUrl,
        Double averageRating,
        Long reviewsCount,
        String ratingLabel,
        Double distanceKm,
        boolean today,
        boolean nearby,
        boolean highRated,
        List<String> categories,
        boolean favorite
) {
}
