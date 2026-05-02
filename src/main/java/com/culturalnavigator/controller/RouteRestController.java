package com.culturalnavigator.controller;

import com.culturalnavigator.api.generated.api.RoutesApi;
import com.culturalnavigator.api.generated.dto.RouteRequest;
import com.culturalnavigator.api.generated.dto.RouteResponse;
import com.culturalnavigator.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RouteRestController implements RoutesApi {

    private final RouteService routeService;

    @Override
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(routeService.findAll());
    }

    @Override
    public ResponseEntity<RouteResponse> getRouteById(Long id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @Override
    public ResponseEntity<RouteResponse> createRoute(RouteRequest routeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(routeRequest));
    }

    @Override
    public ResponseEntity<RouteResponse> updateRoute(Long id, RouteRequest routeRequest) {
        return ResponseEntity.ok(routeService.update(id, routeRequest));
    }

    @Override
    public ResponseEntity<Void> deleteRoute(Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
