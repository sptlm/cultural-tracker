package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Route;

import java.util.List;

public interface RouteCustomRepository {
    List<Route> findByBudgetRangeAndMinItems(int minItems);
}
