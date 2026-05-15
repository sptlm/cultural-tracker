package com.culturalnavigator.controller;

import com.culturalnavigator.config.AppProperties;
import com.culturalnavigator.service.CityDistrictService;
import com.culturalnavigator.service.EventService;
import com.culturalnavigator.service.RouteService;
import com.culturalnavigator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EventService eventService;
    private final RouteService routeService;
    private final UserService userService;
    private final CityDistrictService cityDistrictService;
    private final AppProperties appProperties;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        var user = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("username", principal != null ? principal.getName() : null);
        model.addAttribute("popularEvents", eventService.recommended(user));
        model.addAttribute("personalized", user != null && (!user.getFavoriteCategories().isEmpty()
                || user.getPreferredBudget() != null
                || user.getPreferredAddress() != null));
        cityDistrictService.findAll().stream()
                .filter(district -> district.getName().contains("Вахитов"))
                .findFirst()
                .ifPresent(district -> model.addAttribute("centerDistrictId", district.getId()));
        model.addAttribute("routes", routeService.findPublicRoutes().stream().limit(appProperties.getRoutes().getHomeLimit()).toList());
        return "index";
    }
}
