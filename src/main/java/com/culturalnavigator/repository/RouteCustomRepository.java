package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Route;

import java.math.BigDecimal;
import java.util.List;

public interface RouteCustomRepository {
    List<Route> findByBudgetRangeDurationAndMinItems(BigDecimal minBudget, BigDecimal maxBudget, Integer maxDurationMinutes, int minItems);
}
