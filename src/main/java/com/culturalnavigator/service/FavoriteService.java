package com.culturalnavigator.service;

import com.culturalnavigator.dto.EventCardDto;
import com.culturalnavigator.entity.Event;
import com.culturalnavigator.entity.Favorite;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final EventService eventService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<EventCardDto> currentUserFavorites() {
        User user = userService.currentUser();
        return favoriteRepository.findByUserUsernameOrderByCreatedAtDesc(user.getUsername()).stream()
                .map(favorite -> eventService.toCard(favorite.getEvent(), user))
                .toList();
    }

    @CacheEvict(value = "popularEvents", allEntries = true)
    @Transactional
    public Map<String, Object> toggle(Long eventId) {
        User user = userService.currentUser();
        return favoriteRepository.findByUserIdAndEventId(user.getId(), eventId)
                .map(favorite -> {
                    favoriteRepository.delete(favorite);
                    return Map.<String, Object>of("status", "ok", "favorite", false);
                })
                .orElseGet(() -> {
                    Event event = eventService.findEntity(eventId);
                    Favorite favorite = new Favorite();
                    favorite.setUser(user);
                    favorite.setEvent(event);
                    favoriteRepository.save(favorite);
                    return Map.<String, Object>of("status", "ok", "favorite", true);
                });
    }
}
