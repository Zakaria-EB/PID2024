package com.reservations.reservations.dto;



import com.reservations.reservations.model.Review;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private String review;
    private Integer stars;
    private String userLogin;
    private Long showId;
    private LocalDateTime createdAt;

    public static ReviewDTO from(Review r) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setReview(r.getReview());
        dto.setStars(r.getStars());
        dto.setUserLogin(r.getUser().getLogin());
        dto.setShowId(r.getShow().getId());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
