package com.culturalnavigator.controller;

import com.culturalnavigator.dto.EventForm;
import com.culturalnavigator.dto.VenueForm;
import com.culturalnavigator.entity.Venue;
import com.culturalnavigator.service.CategoryService;
import com.culturalnavigator.service.CityDistrictService;
import com.culturalnavigator.service.EventService;
import com.culturalnavigator.service.ReviewService;
import com.culturalnavigator.service.RouteService;
import com.culturalnavigator.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final VenueService venueService;
    private final EventService eventService;
    private final ReviewService reviewService;
    private final RouteService routeService;
    private final CityDistrictService cityDistrictService;
    private final CategoryService categoryService;

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("events", eventService.findAllCards(null));
        model.addAttribute("reviews", reviewService.findAllForModeration());
        model.addAttribute("routes", routeService.findAllSummariesForAdmin());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("districts", cityDistrictService.findAll());
        return "admin/index";
    }

    @GetMapping("/admin/venues/new")
    public String venueForm(Model model) {
        if (!model.containsAttribute("venueForm")) {
            model.addAttribute("venueForm", new VenueForm());
        }
        addVenueModel(model);
        return "admin/venue-form";
    }

    @PostMapping("/admin/venues")
    public String createVenue(@Valid @ModelAttribute VenueForm venueForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addVenueModel(model);
            return "admin/venue-form";
        }
        venueService.create(venueForm);
        redirectAttributes.addFlashAttribute("success", "Площадка создана");
        return "redirect:/admin";
    }

    @GetMapping("/admin/venues/{id}/edit")
    public String editVenue(@PathVariable Long id, Model model) {
        Venue venue = venueService.findById(id);
        VenueForm form = new VenueForm();
        form.setName(venue.getName());
        form.setDescription(venue.getDescription());
        form.setAddress(venue.getAddress());
        form.setCityDistrictId(venue.getCityDistrict() == null ? null : venue.getCityDistrict().getId());
        model.addAttribute("venueForm", form);
        model.addAttribute("venueId", id);
        addVenueModel(model);
        return "admin/venue-form";
    }

    @PostMapping("/admin/venues/{id}")
    public String updateVenue(@PathVariable Long id, @Valid @ModelAttribute VenueForm venueForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("venueId", id);
            addVenueModel(model);
            return "admin/venue-form";
        }
        venueService.update(id, venueForm);
        redirectAttributes.addFlashAttribute("success", "Площадка обновлена");
        return "redirect:/admin";
    }

    @GetMapping("/admin/events/new")
    public String eventForm(Model model) {
        if (!model.containsAttribute("eventForm")) {
            model.addAttribute("eventForm", new EventForm());
        }
        addEventModel(model);
        return "admin/event-form";
    }

    @PostMapping("/admin/events")
    public String createEvent(@Valid @ModelAttribute EventForm eventForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addEventModel(model);
            return "admin/event-form";
        }
        eventService.create(eventForm);
        redirectAttributes.addFlashAttribute("success", "Мероприятие создано");
        return "redirect:/admin";
    }

    @GetMapping("/admin/events/{id}/edit")
    public String editEvent(@PathVariable Long id, Model model) {
        model.addAttribute("eventForm", eventService.toForm(id));
        model.addAttribute("eventId", id);
        addEventModel(model);
        return "admin/event-form";
    }

    @PostMapping("/admin/events/{id}")
    public String updateEvent(@PathVariable Long id, @Valid @ModelAttribute EventForm eventForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventId", id);
            addEventModel(model);
            return "admin/event-form";
        }
        eventService.update(id, eventForm);
        redirectAttributes.addFlashAttribute("success", "Мероприятие обновлено");
        return "redirect:/admin";
    }

    @PostMapping("/admin/categories")
    public String createCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        categoryService.create(name);
        redirectAttributes.addFlashAttribute("success", "Категория создана");
        return "redirect:/admin";
    }

    @PostMapping("/admin/categories/{id}")
    public String updateCategory(@PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        categoryService.update(id, name);
        redirectAttributes.addFlashAttribute("success", "Категория обновлена");
        return "redirect:/admin";
    }

    @PostMapping("/admin/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Категория удалена");
        return "redirect:/admin";
    }

    @PostMapping("/admin/districts")
    public String createDistrict(@RequestParam String name, RedirectAttributes redirectAttributes) {
        cityDistrictService.create(name);
        redirectAttributes.addFlashAttribute("success", "Район создан");
        return "redirect:/admin";
    }

    @PostMapping("/admin/districts/{id}")
    public String updateDistrict(@PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        cityDistrictService.update(id, name);
        redirectAttributes.addFlashAttribute("success", "Район обновлён");
        return "redirect:/admin";
    }

    @PostMapping("/admin/districts/{id}/delete")
    public String deleteDistrict(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        cityDistrictService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Район удалён");
        return "redirect:/admin";
    }

    @PostMapping("/admin/routes/{id}/delete")
    public String deleteRoute(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        routeService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Маршрут удалён");
        return "redirect:/admin";
    }

    @PostMapping("/admin/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Отзыв удалён");
        return "redirect:/admin";
    }

    private void addVenueModel(Model model) {
        model.addAttribute("districts", cityDistrictService.findAll());
    }

    private void addEventModel(Model model) {
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("categories", categoryService.findAll());
    }
}
