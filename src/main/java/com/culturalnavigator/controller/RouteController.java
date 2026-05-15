package com.culturalnavigator.controller;

import com.culturalnavigator.dto.RouteForm;
import com.culturalnavigator.service.EventService;
import com.culturalnavigator.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;
    private final EventService eventService;

    @GetMapping("/routes")
    public String routes(Model model) {
        model.addAttribute("routes", routeService.findPublicRoutes());
        return "routes/list";
    }

    @GetMapping("/routes/{id}")
    public String route(@PathVariable Long id, Model model) {
        model.addAttribute("route", routeService.findSummary(id));
        return "routes/detail";
    }

    @GetMapping("/routes/new")
    public String createForm(Model model) {
        if (!model.containsAttribute("routeForm")) {
            model.addAttribute("routeForm", new RouteForm());
        }
        addFormModel(model);
        return "routes/form";
    }

    @PostMapping("/routes")
    public String create(@Valid @ModelAttribute RouteForm routeForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addFormModel(model);
            return "routes/form";
        }
        Long routeId = routeService.create(routeForm).id();
        redirectAttributes.addFlashAttribute("success", "Маршрут создан");
        return "redirect:/routes/" + routeId;
    }

    @GetMapping("/routes/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("routeForm")) {
            model.addAttribute("routeForm", routeService.toForm(id));
        }
        model.addAttribute("routeId", id);
        addFormModel(model);
        return "routes/form";
    }

    @PostMapping("/routes/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute RouteForm routeForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("routeId", id);
            addFormModel(model);
            return "routes/form";
        }
        routeService.update(id, routeForm);
        redirectAttributes.addFlashAttribute("success", "Маршрут обновлён");
        return "redirect:/routes/" + id;
    }

    @PostMapping("/routes/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        routeService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Маршрут удалён");
        return "redirect:/profile";
    }

    private void addFormModel(Model model) {
        model.addAttribute("events", eventService.findAllEntities());
    }
}
