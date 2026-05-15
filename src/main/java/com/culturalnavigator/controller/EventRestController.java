package com.culturalnavigator.controller;

import com.culturalnavigator.dto.EventFilter;
import com.culturalnavigator.dto.ReviewRequest;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.service.EventService;
import com.culturalnavigator.service.ReviewService;
import com.culturalnavigator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EventRestController {

    private final EventService eventService;
    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/api/events/search")
    public List<?> search(@ModelAttribute EventFilter filter, Principal principal) {
        User user = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        return eventService.search(filter, user);
    }

    @GetMapping("/api/events/{eventId}/reviews")
    public List<Map<String, Object>> reviews(@PathVariable Long eventId) {
        return reviewService.findForEvent(eventId);
    }

    @PostMapping("/api/events/{eventId}/reviews")
    public Map<String, Object> saveReview(@PathVariable Long eventId, @Valid @RequestBody ReviewRequest request) {
        return Map.of("status", "ok", "review", reviewService.save(eventId, request));
    }
}
