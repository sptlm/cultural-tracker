package com.culturalnavigator.service;

import com.culturalnavigator.dto.RegistrationRequest;
import com.culturalnavigator.entity.Role;
import com.culturalnavigator.entity.User;
import com.culturalnavigator.repository.RoleRepository;
import com.culturalnavigator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
