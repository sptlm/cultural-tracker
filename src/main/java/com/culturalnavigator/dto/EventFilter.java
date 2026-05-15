package com.culturalnavigator.dto;

import com.culturalnavigator.converter.EventFilterMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EventFilter {
    private String query;
    private Long categoryId;
    private Long districtId;
    private boolean freeOnly;
    private EventFilterMode time = EventFilterMode.ALL;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private Double ratingMin;
}
