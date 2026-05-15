package com.culturalnavigator.service;

import com.culturalnavigator.dto.ProfileForm;
import com.culturalnavigator.dto.RegistrationRequest;
import com.culturalnavigator.entity.Category;
import com.culturalnavigator.entity.Role;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.exception.AccessDeniedAppException;
import com.culturalnavigator.exception.NotFoundException;
import com.culturalnavigator.repository.CategoryRepository;
import com.culturalnavigator.repository.RoleRepository;
import com.culturalnavigator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final YandexMapsClient yandexMapsClient;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(toAuthorities(user.getRoles()))
                .build();
    }

    @Transactional
    public void register(RegistrationRequest request, BindingResult bindingResult) {
        validateRegistration(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return;
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(getOrCreateRole(DEFAULT_ROLE));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedAppException("Требуется вход");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + authentication.getName()));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public ProfileForm currentProfileForm() {
        User user = currentUser();
        ProfileForm form = new ProfileForm();
        form.setEmail(user.getEmail());
        form.setPreferredBudget(user.getPreferredBudget());
        form.setPreferredAddress(user.getPreferredAddress());
        form.setFavoriteCategoryIds(user.getFavoriteCategories().stream().map(Category::getId).toList());
        form.setFavoriteCategoryNames(user.getFavoriteCategories().stream()
                .map(Category::getName)
                .sorted()
                .toList());
        return form;
    }

    @Transactional
    public void updateProfile(ProfileForm form, BindingResult bindingResult) {
        User user = currentUser();
        String email = form.getEmail() == null ? "" : form.getEmail().trim().toLowerCase();
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> bindingResult.rejectValue("email", "email.exists", "Пользователь с таким email уже существует"));
        if (bindingResult.hasErrors()) {
            return;
        }
        user.setEmail(email);
        user.setPreferredBudget(form.getPreferredBudget());
        user.setPreferredAddress(form.getPreferredAddress() == null || form.getPreferredAddress().isBlank() ? null : form.getPreferredAddress().trim());
        if (user.getPreferredAddress() == null) {
            user.setPreferredLatitude(null);
            user.setPreferredLongitude(null);
        } else {
            yandexMapsClient.geocode(user.getPreferredAddress()).ifPresent(result -> {
                user.setPreferredLongitude(result.longitude());
                user.setPreferredLatitude(result.latitude());
            });
        }
        user.getFavoriteCategories().clear();
        if (form.getFavoriteCategoryIds() != null) {
            form.getFavoriteCategoryIds().stream().distinct()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new NotFoundException("Категория не найдена")))
                    .forEach(user.getFavoriteCategories()::add);
        }
        userRepository.save(user);
    }

    public boolean currentUserIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    @Transactional(readOnly = true)
    public boolean usernameAvailable(String username) {
        return username != null && !username.isBlank() && !userRepository.existsByUsername(username.trim());
    }

    @Transactional(readOnly = true)
    public boolean emailAvailable(String email) {
        return email != null && !email.isBlank() && !userRepository.existsByEmail(email.trim().toLowerCase());
    }

    private void validateRegistration(RegistrationRequest request, BindingResult bindingResult) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();
        String password = request.getPassword();
        String passwordConfirmation = request.getPasswordConfirmation();

        if (password != null && passwordConfirmation != null && !password.equals(passwordConfirmation)) {
            bindingResult.rejectValue("passwordConfirmation", "password.mismatch", "Пароли не совпадают");
        }
        if (!username.isBlank() && userRepository.existsByUsername(username)) {
            bindingResult.rejectValue("username", "username.exists", "Пользователь с таким именем уже существует");
        }
        if (!email.isBlank() && userRepository.existsByEmail(email)) {
            bindingResult.rejectValue("email", "email.exists", "Пользователь с таким email уже существует");
        }
    }

    private Role getOrCreateRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roleRepository.save(role);
        });
    }

    private Collection<? extends GrantedAuthority> toAuthorities(Set<Role> roles) {
        if (roles.isEmpty()) {
            return Set.of(new SimpleGrantedAuthority("ROLE_" + DEFAULT_ROLE));
        }

        return roles.stream()
                .map(Role::getName)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
