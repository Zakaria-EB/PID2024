package com.reservations.reservations.config;

import com.reservations.reservations.model.User;
import com.reservations.reservations.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByLogin(username);
        if (u == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
        }

        String role = String.valueOf(u.getRole());
        if (role == null || role.isBlank()) {
            throw new UsernameNotFoundException("Utilisateur " + username + " n'a pas de rôle !");
        }
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getLogin())
                .password(u.getPassword())
                .authorities(new SimpleGrantedAuthority(role))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}