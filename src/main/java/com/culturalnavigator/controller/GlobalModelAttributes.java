package com.culturalnavigator.controller;

import com.culturalnavigator.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final AppProperties appProperties;

    @ModelAttribute("username")
    public String username(Principal principal) {
        return principal == null ? null : principal.getName();
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Principal principal) {
        if (principal instanceof org.springframework.security.core.Authentication authentication) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        }
        return false;
    }

    @ModelAttribute("contextPath")
    public String contextPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    @ModelAttribute("frontendConfig")
    public Map<String, Object> frontendConfig() {
        return Map.of(
                "nearbyDistanceKm", appProperties.getEvents().getNearbyDistanceKm(),
                "highRatingThreshold", appProperties.getEvents().getHighRatingThreshold(),
                "earthRadiusKm", appProperties.getGeo().getEarthRadiusKm(),
                "distanceScale", appProperties.getGeo().getDistanceScale(),
                "minRoutePoints", appProperties.getGeo().getMinRoutePoints(),
                "toastDurationMs", appProperties.getUi().getToastDurationMs(),
                "actionToastDurationMs", appProperties.getUi().getActionToastDurationMs()
        );
    }
}
