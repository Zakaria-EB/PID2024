package com.reservations.reservations.controller;



import com.reservations.reservations.model.Representation;
import com.reservations.reservations.model.Reservation;
import com.reservations.reservations.model.Show;
import com.reservations.reservations.repository.UserRepository;
import com.reservations.reservations.service.PriceService;
import com.reservations.reservations.service.RepresentationService;
import com.reservations.reservations.service.ReservationService;
import com.reservations.reservations.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/shows/{showId}/reserve")
public class ShowReservationController {
    @Autowired
    private ShowService showService;
    @Autowired
    private RepresentationService repService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepo;

    /**
     * Affiche le formulaire de réservation : choix de la représentation, du type de tarif, du nombre de places.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showForm(@PathVariable Long showId, Model model) {
        Show show = showService.get(showId.toString());
        if (show == null || !show.isBookable()) {
            model.addAttribute("errorMessage", "Spectacle non trouvable ou non réservable.");
            return "error/404";
        }
        model.addAttribute("show", show);

        List<Representation> reps = repService.getFromShow(show);
        model.addAttribute("representations", reps);

        model.addAttribute("prices", show.getPrices());
        model.addAttribute("form", new ReservationForm());
        return "reservation/form";
    }

    /**
     * Page de confirmation
     */
    @GetMapping("/confirmation/{resId}")
    public String confirm(@PathVariable Long resId, Model model) {
        Reservation res = reservationService.getReservation(resId);
        if (res == null) {
            model.addAttribute("errorMessage", "Réservation introuvable.");
            return "error/404";
        }
        model.addAttribute("reservation", res);
        return "reservation/confirmation";
    }

    /**
     * Petit DTO pour lier le formulaire Thymeleaf
     */
    public static class ReservationForm {
        private Long representationId;
        private Long priceId;
        private Integer quantity;
        // getters & setters…
    }
}
