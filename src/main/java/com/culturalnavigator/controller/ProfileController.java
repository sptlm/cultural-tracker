package com.culturalnavigator.controller;

import com.culturalnavigator.dto.ProfileForm;
import com.culturalnavigator.service.CategoryService;
import com.culturalnavigator.service.FavoriteService;
import com.culturalnavigator.service.RouteService;
import com.culturalnavigator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final RouteService routeService;
    private final FavoriteService favoriteService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("routes", routeService.findCurrentUserRoutes());
        model.addAttribute("favorites", favoriteService.currentUserFavorites());
        model.addAttribute("profileForm", userService.currentProfileForm());
        return "profile/index";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", userService.currentProfileForm());
        }
        model.addAttribute("categories", categoryService.findAll());
        return "profile/edit";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute ProfileForm profileForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        userService.updateProfile(profileForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "profile/edit";
        }
        redirectAttributes.addFlashAttribute("success", "Профиль обновлён");
        return "redirect:/profile";
    }
}
