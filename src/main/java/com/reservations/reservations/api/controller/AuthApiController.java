package com.reservations.reservations.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build(); // non connecté
        }
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map( a -> a.getAuthority() )
                .collect( Collectors.toList() );

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities().stream()
                        .map( a -> a.getAuthority() )
                        .toList()
        ));
    }

    @GetMapping("/csrf/token")
    public ResponseEntity<Void> csrf(HttpServletRequest request) {
        // Accéder au HttpServletRequest force Spring à générer le token
        return ResponseEntity.ok().build();
    }




}
