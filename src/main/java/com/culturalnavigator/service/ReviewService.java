package com.culturalnavigator.service;

import com.culturalnavigator.dto.ReviewRequest;
import com.culturalnavigator.entity.Event;
import com.culturalnavigator.entity.Review;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventService eventService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findForEvent(Long eventId) {
        eventService.findEntity(eventId);
        return reviewRepository.findByEventIdOrderByCreatedAtDesc(eventId).stream()
                .map(this::toMap)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAllForModeration() {
        return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toModerationMap)
                .toList();
    }

    @CacheEvict(value = {"popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public Map<String, Object> save(Long eventId, ReviewRequest request) {
        User user = userService.currentUser();
        Event event = eventService.findEntity(eventId);
        Review review = reviewRepository.findByAuthorIdAndEventId(user.getId(), eventId).orElseGet(Review::new);
        review.setAuthor(user);
        review.setEvent(event);
        review.setRating(request.getRating());
        review.setText(request.getText().trim());
        return toMap(reviewRepository.save(review));
    }

    @CacheEvict(value = {"popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    private Map<String, Object> toMap(Review review) {
        return Map.of(
                "id", review.getId(),
                "author", review.getAuthor().getUsername(),
                "rating", review.getRating(),
                "text", review.getText(),
                "createdAt", review.getCreatedAt()
        );
    }

    private Map<String, Object> toModerationMap(Review review) {
        return Map.of(
                "id", review.getId(),
                "author", review.getAuthor().getUsername(),
                "eventId", review.getEvent().getId(),
                "eventTitle", review.getEvent().getTitle(),
                "rating", review.getRating(),
                "text", review.getText(),
                "createdAt", review.getCreatedAt()
        );
    }
}
