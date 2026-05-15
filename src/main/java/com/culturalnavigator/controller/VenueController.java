package com.culturalnavigator.controller;

import com.culturalnavigator.service.VenueService;
import com.culturalnavigator.service.EventService;
import com.culturalnavigator.service.UserService;
import com.culturalnavigator.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;
    private final EventService eventService;
    private final UserService userService;

    @GetMapping("/venues")
    public String venues(Model model) {
        model.addAttribute("venues", venueService.findAll());
        return "venues/list";
    }

    @GetMapping("/venues/{id}")
    public String venue(@PathVariable Long id, Model model, Principal principal) {
        User user = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("venue", venueService.findById(id));
        model.addAttribute("events", eventService.findByVenue(id, user));
        return "venues/detail";
    }
}
