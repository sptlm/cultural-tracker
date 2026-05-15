package com.culturalnavigator.controller;

import com.culturalnavigator.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FavoriteRestController {

    private final FavoriteService favoriteService;

    @PostMapping("/api/favorites/events/{eventId}")
    public Map<String, Object> toggle(@PathVariable Long eventId) {
        return favoriteService.toggle(eventId);
    }
}
