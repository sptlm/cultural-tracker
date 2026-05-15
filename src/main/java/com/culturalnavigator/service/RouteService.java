package com.culturalnavigator.service;

import com.culturalnavigator.config.AppProperties;
import com.culturalnavigator.api.generated.dto.RouteRequest;
import com.culturalnavigator.api.generated.dto.RouteResponse;
import com.culturalnavigator.dto.EventCardDto;
import com.culturalnavigator.dto.RouteForm;
import com.culturalnavigator.dto.RouteSummaryDto;
import com.culturalnavigator.entity.Event;
import com.culturalnavigator.entity.Route;
import com.culturalnavigator.entity.RouteItem;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.exception.AccessDeniedAppException;
import com.culturalnavigator.exception.NotFoundException;
import com.culturalnavigator.repository.EventRepository;
import com.culturalnavigator.repository.RouteRepository;
import com.culturalnavigator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventService eventService;
    private final YandexMapsClient yandexMapsClient;
    private final AppProperties appProperties;

    @Transactional(readOnly = true)
    public List<RouteResponse> findAll() {
        if (userService.currentUserIsAdmin()) {
            return routeRepository.findAll().stream().map(this::toResponse).toList();
        }
        User currentUser = userService.currentUser();
        return routeRepository.findVisibleForUsername(currentUser.getUsername()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RouteResponse findById(Long id) {
        Route route = getRouteOrThrow(id);
        assertCanView(route);
        return toResponse(route);
    }

    @Cacheable("topRoutes")
    @Transactional(readOnly = true)
    public List<RouteSummaryDto> findPublicRoutes() {
        return routeRepository.findByIsPublicTrueOrderByCreatedAtDesc().stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RouteSummaryDto> findAllSummariesForAdmin() {
        if (!userService.currentUserIsAdmin()) {
            throw new AccessDeniedAppException("Доступ запрещён");
        }
        return routeRepository.findAll().stream()
                .sorted(Comparator.comparing(Route::getCreatedAt).reversed())
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RouteSummaryDto> findCurrentUserRoutes() {
        return routeRepository.findByAuthorUsernameOrderByCreatedAtDesc(userService.currentUser().getUsername()).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public RouteSummaryDto findSummary(Long id) {
        Route route = getRouteOrThrow(id);
        assertCanView(route);
        return toSummary(route);
    }

    @Transactional(readOnly = true)
    public RouteForm toForm(Long id) {
        Route route = getRouteOrThrow(id);
        assertCanManage(route);
        RouteForm form = new RouteForm();
        form.setTitle(route.getTitle());
        form.setDescription(route.getDescription());
        form.setBudget(route.getBudget());
        form.setDurationMinutes(route.getDurationMinutes());
        form.setIsPublic(route.getIsPublic());
        form.setEventIds(route.getItems().stream().map(item -> item.getEvent().getId()).toList());
        return form;
    }

    @CacheEvict(value = "topRoutes", allEntries = true)
    @Transactional
    public RouteSummaryDto create(RouteForm form) {
        Route route = new Route();
        route.setAuthor(userService.currentUser());
        applyForm(route, form);
        return toSummary(routeRepository.save(route));
    }

    @CacheEvict(value = "topRoutes", allEntries = true)
    @Transactional
    public RouteResponse create(RouteRequest request) {
        Route route = new Route();
        route.setAuthor(resolveAuthor(request.getAuthorId()));
        applyApiRequest(route, request);
        return toResponse(routeRepository.save(route));
    }

    @CacheEvict(value = "topRoutes", allEntries = true)
    @Transactional
    public RouteSummaryDto update(Long id, RouteForm form) {
        Route route = getRouteOrThrow(id);
        assertCanManage(route);
        applyForm(route, form);
        return toSummary(routeRepository.save(route));
    }

    @CacheEvict(value = "topRoutes", allEntries = true)
    @Transactional
    public RouteResponse update(Long id, RouteRequest request) {
        Route route = getRouteOrThrow(id);
        assertCanManage(route);
        if (userService.currentUserIsAdmin() && request.getAuthorId() != null && request.getAuthorId() > 0) {
            route.setAuthor(resolveAuthor(request.getAuthorId()));
        }
        applyApiRequest(route, request);
        return toResponse(routeRepository.save(route));
    }

    @CacheEvict(value = "topRoutes", allEntries = true)
    @Transactional
    public void delete(Long id) {
        Route route = getRouteOrThrow(id);
        assertCanManage(route);
        routeRepository.delete(route);
    }

    private Route getRouteOrThrow(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Маршрут с id=" + id + " не найден"));
    }

    private User resolveAuthor(Long authorId) {
        User currentUser = userService.currentUser();
        if (!userService.currentUserIsAdmin()) {
            return currentUser;
        }
        if (authorId != null && authorId > 0) {
            return userRepository.findById(authorId)
                    .orElseThrow(() -> new NotFoundException("Автор с id=" + authorId + " не найден"));
        }
        return currentUser;
    }

    private void assertCanManage(Route route) {
        User currentUser = userService.currentUser();
        if (!userService.currentUserIsAdmin() && !Objects.equals(route.getAuthor().getId(), currentUser.getId())) {
            throw new AccessDeniedAppException("Можно редактировать только свои маршруты");
        }
    }

    private void assertCanView(Route route) {
        if (Boolean.TRUE.equals(route.getIsPublic())) {
            return;
        }
        User currentUser = userService.currentUser();
        if (!userService.currentUserIsAdmin() && !Objects.equals(route.getAuthor().getId(), currentUser.getId())) {
            throw new AccessDeniedAppException("Маршрут доступен только автору");
        }
    }

    private void applyApiRequest(Route route, RouteRequest request) {
        route.setTitle(request.getTitle().trim());
        route.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());
        route.setDurationMinutes(request.getDurationMinutes() == null ? appProperties.getRoutes().getDefaultApiDurationMinutes() : request.getDurationMinutes());
        route.setBudget(validateMoney(request.getBudget() == null ? BigDecimal.ZERO : request.getBudget()));
        route.setIsPublic(request.getIsPublic() == null || request.getIsPublic());
        replaceItems(route, request.getEventIds() == null ? List.of() : request.getEventIds());
    }

    private void applyForm(Route route, RouteForm form) {
        route.setTitle(form.getTitle().trim());
        route.setDescription(form.getDescription().trim());
        route.setDurationMinutes(form.getDurationMinutes());
        route.setBudget(validateMoney(form.getBudget()));
        route.setIsPublic(Boolean.TRUE.equals(form.getIsPublic()));
        replaceItems(route, form.getEventIds());
    }

    private void replaceItems(Route route, List<Long> eventIds) {
        route.getItems().clear();
        List<GeocodingResult> points = new ArrayList<>();
        int position = 1;
        for (Long eventId : eventIds.stream().distinct().toList()) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
            RouteItem item = new RouteItem();
            item.setRoute(route);
            item.setEvent(event);
            item.setPosition(position++);
            route.getItems().add(item);
            if (event.getVenue().getLongitude() != null && event.getVenue().getLatitude() != null) {
                points.add(new GeocodingResult(event.getVenue().getLongitude(), event.getVenue().getLatitude()));
            }
        }
        route.setDistanceKm(yandexMapsClient.estimateDistanceKm(points));
    }

    private BigDecimal validateMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(appProperties.getMoney().getMax()) > 0) {
            throw new IllegalArgumentException("Сумма должна быть от 0 до 1 000 000");
        }
        return value;
    }

    private RouteResponse toResponse(Route route) {
        RouteResponse response = new RouteResponse();
        response.setId(route.getId());
        response.setTitle(route.getTitle());
        response.setDescription(route.getDescription());
        response.setDurationMinutes(route.getDurationMinutes());
        response.setBudget(route.getBudget());
        response.setIsPublic(route.getIsPublic());
        response.setDistanceKm(route.getDistanceKm());
        response.setAuthorId(route.getAuthor().getId());
        response.setAuthorUsername(route.getAuthor().getUsername());
        response.setItemsCount(route.getItems().size());
        response.setEventIds(route.getItems().stream().map(item -> item.getEvent().getId()).toList());
        return response;
    }

    private RouteSummaryDto toSummary(Route route) {
        List<EventCardDto> events = route.getItems().stream()
                .map(RouteItem::getEvent)
                .map(event -> eventService.toCard(event, null))
                .toList();
        return new RouteSummaryDto(
                route.getId(),
                route.getTitle(),
                route.getDescription(),
                route.getAuthor().getUsername(),
                route.getDurationMinutes(),
                route.getBudget(),
                route.getIsPublic(),
                route.getDistanceKm(),
                events
        );
    }
}
