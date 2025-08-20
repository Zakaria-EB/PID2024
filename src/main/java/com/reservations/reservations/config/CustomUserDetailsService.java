//package com.reservations.reservations.config;
//
//import com.reservations.reservations.model.User;
//import com.reservations.reservations.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        final User user = userRepository.findByLogin(username);
//
//        if (user == null) {
//            throw new UsernameNotFoundException("User " + username + " not found");
//        }
//
//        return new org.springframework.security.core.userdetails.User(
//                username,
//                user.getPassword(),
//                getGrantedAuthorities(user.getRole().toString()));
//    }
//
//    private List<GrantedAuthority> getGrantedAuthorities(String role) {
//        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
//
//        return authorities;
//    }
//
//}
package com.reservations.reservations.config;

import com.reservations.reservations.model.User;
import com.reservations.reservations.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.reservations.reservations.model.User u = userRepository.findByLogin(username);
        if (u == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
        }

        // ---- PATCH NPE ICI ----
        // Si le rôle est null -> on met USER par défaut
        String role = (u.getRole() == null) ? "USER" : u.getRole().toString();
        // Toujours préfixer par ROLE_
        if (!role.startsWith("ROLE_")) role = "ROLE_" + role;

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false);

        return builder.build();
    }
}
