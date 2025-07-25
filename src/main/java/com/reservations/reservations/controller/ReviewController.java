package com.reservations.reservations.controller;

import com.reservations.reservations.model.UserRole;
import com.reservations.reservations.model.Review;
import com.reservations.reservations.model.Show;
import com.reservations.reservations.model.User;
import com.reservations.reservations.service.ReviewService;
import com.reservations.reservations.service.ShowService;
import com.reservations.reservations.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ShowService showService;

    @Autowired
    private UserService userService;

    @PostMapping("/submit")
    public String submitReview(@RequestParam Long showId,
                               @RequestParam String review,
                               @RequestParam int stars,
                               @AuthenticationPrincipal UserDetails currentUser) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = userService.findByLogin(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        Show show = showService.get(showId.toString());
        if (show == null) {
            return "redirect:/dev/shows";
        }

        Review r = new Review();
        r.setUser(user);
        r.setShow(show);
        r.setReview(review);
        r.setStars(stars);
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());

        reviewService.addReview(r);

        return "redirect:/dev/shows/" + showId;
    }

    @PostMapping("/update")
    public String updateReview(@RequestParam("reviewId") Long id,
                               @RequestParam("review") String reviewText,
                               @RequestParam("stars") int stars,
                               Principal principal) {

        Review review = reviewService.getReview(id);
        if (review != null && review.getUser().getLogin().equals(principal.getName())) {
            review.setReview(reviewText);
            review.setStars(stars);
            review.setUpdatedAt(LocalDateTime.now());
            reviewService.addReview(review);
        }

        return "redirect:/dev/shows/" + review.getShow().getId();
    }

    @PostMapping("/delete")
    public String deleteReview(@RequestParam Long reviewId, Principal principal) {
        Review review = reviewService.getReview(reviewId);

        if (review != null) {
            String currentUsername = principal.getName();
            User currentUser = userService.findByLogin(currentUsername);

            boolean isOwner = review.getUser().getLogin().equals(currentUsername);
            boolean isAdmin = currentUser != null && currentUser.hasRole(UserRole.ADMIN);

            if (isOwner || isAdmin) {
                reviewService.deleteReview(reviewId);
            }
        }

        return "redirect:/dev/shows/" + (review != null ? review.getShow().getId() : "");
    }
}
