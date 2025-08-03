package com.reservations.reservations.api.controller;


import com.reservations.reservations.dto.ReviewDTO;
import com.reservations.reservations.model.Review;
import com.reservations.reservations.model.Show;
import com.reservations.reservations.model.User;
import com.reservations.reservations.model.UserRole;
import com.reservations.reservations.service.ReviewService;
import com.reservations.reservations.service.ShowService;
import com.reservations.reservations.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ShowService showService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<ReviewDTO> getReviewsForShow(@RequestParam Long showId) {
        return reviewService.getReviewsByShowId(showId)
                .stream()
                .map(ReviewDTO::from)
                .collect(Collectors.toList());

    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitReview(@RequestBody ReviewDTO dto, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByLogin(currentUser.getUsername());
        Show show = showService.get(String.valueOf(dto.getShowId()));
        if (user == null || show == null) {
            return ResponseEntity.badRequest().build();
        }

        Review r = new Review();
        r.setUser(user);
        r.setShow(show);
        r.setReview(dto.getReview());
        r.setStars(dto.getStars());
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());

        reviewService.addReview(r);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Principal principal) {
        Review review = reviewService.getReview(id);
        if (review == null) return ResponseEntity.notFound().build();

        String username = principal.getName();
        User user = userService.findByLogin(username);

        boolean isOwner = review.getUser().getLogin().equals(username);
        boolean isAdmin = user != null && user.hasRole(UserRole.ADMIN);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
    public static ReviewDTO from(Review r) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setReview(r.getReview());
        dto.setStars(r.getStars());
        dto.setUserLogin(r.getUser().getLogin()); // ⚠️ Important
        dto.setShowId(r.getShow().getId());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

}