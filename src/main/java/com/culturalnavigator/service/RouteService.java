package com.culturalnavigator.service;

import com.culturalnavigator.api.generated.dto.RouteRequest;
import com.culturalnavigator.api.generated.dto.RouteResponse;
import com.culturalnavigator.entity.Route;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.exception.NotFoundException;
import com.culturalnavigator.repository.RouteRepository;
import com.culturalnavigator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RouteResponse> findAll() {
        return routeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RouteResponse findById(Long id) {
        return toResponse(getRouteOrThrow(id));
    }

    @Transactional
    public RouteResponse create(RouteRequest request) {
        User author = resolveAuthor(request.getAuthorId());

        Route route = new Route();
        route.setTitle(request.getTitle());
        route.setAuthor(author);
        return toResponse(routeRepository.save(route));
    }

    @Transactional
    public RouteResponse update(Long id, RouteRequest request) {
        Route route = getRouteOrThrow(id);
        User author = resolveAuthor(request.getAuthorId());

        route.setTitle(request.getTitle());
        route.setAuthor(author);
        return toResponse(routeRepository.save(route));
    }

    @Transactional
    public void delete(Long id) {
        Route route = getRouteOrThrow(id);
        routeRepository.delete(route);
    }

    private Route getRouteOrThrow(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Маршрут с id=" + id + " не найден"));
    }

    private User resolveAuthor(Long authorId) {
        if (authorId != null && authorId > 0) {
            return userRepository.findById(authorId)
                    .orElseThrow(() -> new NotFoundException("Автор с id=" + authorId + " не найден"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new NotFoundException("Текущий пользователь не найден"));
        }

        throw new NotFoundException("Автор маршрута не найден");
    }

    private RouteResponse toResponse(Route route) {
        RouteResponse response = new RouteResponse();
        response.setId(route.getId());
        response.setTitle(route.getTitle());
        response.setAuthorId(route.getAuthor().getId());
        response.setAuthorUsername(route.getAuthor().getUsername());
        response.setItemsCount(route.getItems().size());
        return response;
    }
}
