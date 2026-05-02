package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long>, RouteCustomRepository {
}
