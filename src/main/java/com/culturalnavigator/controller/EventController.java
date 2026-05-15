package com.culturalnavigator.controller;

import com.culturalnavigator.dto.EventFilter;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.service.CategoryService;
import com.culturalnavigator.service.CityDistrictService;
import com.culturalnavigator.service.EventService;
import com.culturalnavigator.service.ReviewService;
import com.culturalnavigator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final CityDistrictService cityDistrictService;
    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/events")
    public String events(@ModelAttribute EventFilter filter, Model model, Principal principal) {
        User user = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("events", eventService.search(filter, user));
        model.addAttribute("filter", filter);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("districts", cityDistrictService.findAll());
        return "events/list";
    }

    @GetMapping("/events/{id}")
    public String event(@PathVariable Long id, Model model, Principal principal) {
        User user = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("event", eventService.findCard(id, user));
        model.addAttribute("reviews", reviewService.findForEvent(id));
        return "events/detail";
    }
}
