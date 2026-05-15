package com.culturalnavigator.service;

import com.culturalnavigator.config.AppProperties;
import com.culturalnavigator.dto.EventCardDto;
import com.culturalnavigator.dto.EventFilter;
import com.culturalnavigator.dto.EventForm;
import com.culturalnavigator.entity.Category;
import com.culturalnavigator.entity.Event;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.converter.EventFilterMode;
import com.culturalnavigator.exception.NotFoundException;
import com.culturalnavigator.repository.CategoryRepository;
import com.culturalnavigator.repository.EventRepository;
import com.culturalnavigator.repository.FavoriteRepository;
import com.culturalnavigator.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final FavoriteRepository favoriteRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final YandexMapsClient yandexMapsClient;
    private final AppProperties appProperties;

    @Transactional(readOnly = true)
    public List<EventCardDto> search(EventFilter filter, User user) {
        Long categoryId = filter == null ? null : filter.getCategoryId();
        Long districtId = filter == null ? null : filter.getDistrictId();
        String query = filter == null || filter.getQuery() == null || filter.getQuery().isBlank() ? "" : filter.getQuery().trim();
        EventFilterMode time = filter == null || filter.getTime() == null ? EventFilterMode.ALL : filter.getTime();
        boolean freeOnly = filter != null && (filter.isFreeOnly() || time == EventFilterMode.FREE);
        return eventRepository.search(query, categoryId, districtId, freeOnly).stream()
                .filter(event -> matchesTime(event, time))
                .filter(event -> matchesDateRange(event, filter))
                .filter(event -> matchesPriceRange(event, filter))
                .map(event -> toCard(event, user))
                .filter(event -> matchesRating(event, filter))
                .toList();
    }

    @Cacheable("popularEvents")
    @Transactional(readOnly = true)
    public List<EventCardDto> popular() {
        return eventRepository.findPopular(LocalDateTime.now().minusDays(appProperties.getEvents().getPopularLookbackDays())).stream()
                .limit(appProperties.getEvents().getPopularLimit())
                .map(event -> toCard(event, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventCardDto> recommended(User user) {
        if (user == null || (user.getFavoriteCategories().isEmpty()
                && user.getPreferredBudget() == null
                && user.getPreferredLatitude() == null
                && user.getPreferredLongitude() == null)) {
            return popular();
        }
        return eventRepository.findPopular(LocalDateTime.now().minusDays(appProperties.getEvents().getPopularLookbackDays())).stream()
                .sorted(Comparator
                        .comparingInt((Event event) -> recommendationScore(event, user)).reversed()
                        .thenComparing(event -> distanceToUser(event, user) == null ? Double.MAX_VALUE : distanceToUser(event, user))
                        .thenComparing(Event::getStartAt))
                .limit(appProperties.getEvents().getPopularLimit())
                .map(event -> toCard(event, user))
                .toList();
    }

    @Transactional(readOnly = true)
    public Event findEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    @Transactional(readOnly = true)
    public EventCardDto findCard(Long id, User user) {
        return toCard(findEntity(id), user);
    }

    @Transactional(readOnly = true)
    public List<Event> findAllEntities() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getStartAt))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventCardDto> findAllCards(User user) {
        return findAllEntities().stream()
                .map(event -> toCard(event, user))
                .toList();
    }

    @Transactional(readOnly = true)
    public EventForm toForm(Long id) {
        Event event = findEntity(id);
        EventForm form = new EventForm();
        form.setTitle(event.getTitle());
        form.setDescription(event.getDescription());
        form.setStartAt(event.getStartAt());
        form.setEndAt(event.getEndAt());
        form.setPrice(event.getPrice());
        form.setImageUrl(event.getImageUrl());
        form.setVenueId(event.getVenue().getId());
        form.setCategoryIds(event.getCategories().stream().map(Category::getId).toList());
        return form;
    }

    @CacheEvict(value = {"popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public Event create(EventForm form) {
        Event event = new Event();
        applyForm(event, form);
        return eventRepository.save(event);
    }

    @CacheEvict(value = {"popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public Event update(Long id, EventForm form) {
        Event event = findEntity(id);
        applyForm(event, form);
        return eventRepository.save(event);
    }

    public EventCardDto toCard(Event event, User user) {
        boolean favorite = user != null && favoriteRepository.existsByUserIdAndEventId(user.getId(), event.getId());
        String district = event.getVenue().getCityDistrict() == null ? "Район не указан" : event.getVenue().getCityDistrict().getName();
        Double averageRating = eventRepository.averageRating(event.getId());
        long reviewsCount = eventRepository.countReviewsByEventId(event.getId());
        Double rating = reviewsCount == 0 ? null : Math.round(averageRating * 10.0) / 10.0;
        Double distance = distanceToUser(event, user);
        return new EventCardDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartAt(),
                event.getEndAt(),
                event.getStartLabel(),
                event.getTimeLabel(),
                event.getPrice(),
                event.getVenue().getId(),
                event.getVenue().getName(),
                district,
                event.getImageUrl(),
                rating,
                reviewsCount,
                reviewsCount == 0 ? "Нет оценок" : String.valueOf(rating),
                distance,
                event.getStartAt() != null && event.getStartAt().toLocalDate().equals(LocalDate.now()),
                distance != null && distance <= appProperties.getEvents().getNearbyDistanceKm(),
                rating != null && rating >= appProperties.getEvents().getHighRatingThreshold(),
                event.getCategories().stream().map(Category::getName).sorted().toList(),
                favorite
        );
    }

    private int recommendationScore(Event event, User user) {
        int score = 0;
        Set<Long> favoriteCategoryIds = user.getFavoriteCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        if (!favoriteCategoryIds.isEmpty() && event.getCategories().stream().anyMatch(category -> favoriteCategoryIds.contains(category.getId()))) {
            score += appProperties.getRecommendations().getFavoriteCategoryScore();
        }
        BigDecimal budget = user.getPreferredBudget();
        if (budget != null && event.getPrice().compareTo(budget) <= 0) {
            score += appProperties.getRecommendations().getBudgetScore();
        }
        Double distance = distanceToUser(event, user);
        if (distance != null) {
            if (distance <= appProperties.getRecommendations().getNearDistanceKm()) {
                score += appProperties.getRecommendations().getNearDistanceScore();
            } else if (distance <= appProperties.getRecommendations().getMediumDistanceKm()) {
                score += appProperties.getRecommendations().getMediumDistanceScore();
            }
        }
        return score;
    }

    private Double distanceToUser(Event event, User user) {
        if (user == null
                || user.getPreferredLatitude() == null
                || user.getPreferredLongitude() == null
                || event.getVenue().getLatitude() == null
                || event.getVenue().getLongitude() == null) {
            return null;
        }
        return yandexMapsClient.distanceKm(
                new GeocodingResult(user.getPreferredLongitude(), user.getPreferredLatitude()),
                new GeocodingResult(event.getVenue().getLongitude(), event.getVenue().getLatitude())
        );
    }

    private boolean matchesTime(Event event, EventFilterMode mode) {
        if (mode == EventFilterMode.ALL || mode == EventFilterMode.FREE) {
            return true;
        }
        LocalDateTime startAt = event.getStartAt();
        if (startAt == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (mode) {
            case TODAY -> startAt.toLocalDate().equals(now.toLocalDate());
            case EVENING -> startAt.getHour() >= appProperties.getEvents().getEveningStartsAtHour();
            case WEEKEND -> startAt.getDayOfWeek() == DayOfWeek.SATURDAY || startAt.getDayOfWeek() == DayOfWeek.SUNDAY;
            case UPCOMING -> !startAt.isBefore(now);
            case ALL, FREE -> true;
        };
    }

    private boolean matchesDateRange(Event event, EventFilter filter) {
        if (filter == null || event.getStartAt() == null) {
            return true;
        }
        if (filter.getDateFrom() != null && event.getStartAt().toLocalDate().isBefore(filter.getDateFrom())) {
            return false;
        }
        return filter.getDateTo() == null || !event.getStartAt().toLocalDate().isAfter(filter.getDateTo());
    }

    private boolean matchesPriceRange(Event event, EventFilter filter) {
        if (filter == null || event.getPrice() == null) {
            return true;
        }
        if (filter.getPriceMin() != null && event.getPrice().compareTo(filter.getPriceMin()) < 0) {
            return false;
        }
        return filter.getPriceMax() == null || event.getPrice().compareTo(filter.getPriceMax()) <= 0;
    }

    private boolean matchesRating(EventCardDto event, EventFilter filter) {
        if (filter == null || filter.getRatingMin() == null) {
            return true;
        }
        return event.averageRating() != null && event.averageRating() >= filter.getRatingMin();
    }

    private void applyForm(Event event, EventForm form) {
        event.setTitle(form.getTitle().trim());
        event.setDescription(form.getDescription().trim());
        event.setStartAt(form.getStartAt());
        event.setEndAt(form.getEndAt());
        event.setPrice(validateMoney(form.getPrice()));
        event.setImageUrl(form.getImageUrl() == null || form.getImageUrl().isBlank() ? null : form.getImageUrl().trim());
        event.setVenue(venueRepository.findById(form.getVenueId())
                .orElseThrow(() -> new NotFoundException("Площадка не найдена")));
        event.getCategories().clear();
        if (form.getCategoryIds() != null) {
            form.getCategoryIds().stream().distinct()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new NotFoundException("Категория не найдена")))
                    .forEach(event.getCategories()::add);
        }
    }

    private BigDecimal validateMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(appProperties.getMoney().getMax()) > 0) {
            throw new IllegalArgumentException("Стоимость должна быть от 0 до 1 000 000");
        }
        return value;
    }

    @Transactional(readOnly = true)
    public List<EventCardDto> findByVenue(Long venueId, User user) {
        return eventRepository.findByVenueIdOrderByStartAtAsc(venueId).stream()
                .map(event -> toCard(event, user))
                .toList();
    }
}
