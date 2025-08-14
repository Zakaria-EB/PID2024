package com.reservations.reservations.controller;


import com.reservations.reservations.model.*;
import com.reservations.reservations.repository.RepresentationRepository;
import com.reservations.reservations.service.ReviewService;
import com.reservations.reservations.service.ShowService;
import com.reservations.reservations.service.TagService;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@SessionAttributes("cart")
public class ShowController {
    @ModelAttribute("cart")
    public Cart cart() {
        return new Cart();
    }

    @Autowired
    ShowService service;

    @Autowired
    private RepresentationRepository representationRepo;

    @Autowired
    TagService tagService;

    @Autowired
    private ReviewService reviewService; // 🔧 Ajouté pour les commentaires

    @GetMapping("//shows")
    public String index(@RequestParam(value = "tag", required = false) String tagLabel, Model model) {
        List<Show> shows;
        String title = "Liste des spectacles";

        if (tagLabel != null && !tagLabel.isBlank()) {
            Tag tag = tagService.findByTag(tagLabel).orElse(null);
            if (tag != null) {
                shows = service.getByTag(tag);
                model.addAttribute("resultCount", shows.size());
                title += " – Mots-clés : " + tagLabel;
            } else {
                shows = new ArrayList<>();
                model.addAttribute("errorMessage", "Mot-clé introuvable");
            }
        } else {
            shows = service.getAll();
        }

        model.addAttribute("shows", shows);
        model.addAttribute("title", title);
        model.addAttribute("availableTags", tagService.findAll());

        return "show/index";
    }

    @GetMapping("//shows/{id}")
    @Transactional
    public String show(Model model, @PathVariable("id") String id) {
        Show show = service.getWithAssociations(id);
        if (show == null) {
            model.addAttribute("errorMessage", "Spectacle introuvable.");
            return "error/404";
        }

        Hibernate.initialize(show.getTags());
        show.setTags(new HashSet<>(show.getTags()));

        Set<ArtistType> uniqueArtistTypes = new HashSet<>(show.getArtistTypes());
        Map<String, ArrayList<Artist>> collaborateurs = new TreeMap<>();
        for (ArtistType at : uniqueArtistTypes) {
            String type = at.getType().getType();
            ArrayList<Artist> artistes = collaborateurs.computeIfAbsent(type, k -> new ArrayList<>());
            if (!artistes.contains(at.getArtist())) {
                artistes.add(at.getArtist());
            }
        }

        show.getRepresentations().forEach(rep -> Hibernate.initialize(rep.getItems()));

        boolean canBook = show.getRepresentations().stream()
                .anyMatch(r -> r.getAvailableSeats() > 0);

        model.addAttribute("canBook", canBook);
        model.addAttribute("collaborateurs", collaborateurs);
        model.addAttribute("availableTags", tagService.findAll());
        model.addAttribute("show", show);
        model.addAttribute("title", "Fiche d'un spectacle");

        // 🔧 Ajouter les reviews au modèle sans toucher le reste du contrôleur
        model.addAttribute("reviews", reviewService.getReviewsByShowId(show.getId()));

        return "show/show";
    }

    @PostMapping("/shows/{id}/tags")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String addTagToShow(@PathVariable("id") String id,
                               @RequestParam("tagId") Long tagId,
                               RedirectAttributes redirectAttributes) {
        Show show = service.getWithAssociations(id);
        Tag tag = tagService.find(tagId).orElse(null);

        if (show != null && tag != null) {
            Hibernate.initialize(show.getTags());
            Tag[] tagsArray = show.getTags().toArray(new Tag[0]);
            Set<Tag> updatedTags = new HashSet<>(Arrays.asList(tagsArray));

            if (!updatedTags.contains(tag)) {
                updatedTags.add(tag);
                show.setTags(updatedTags);
                service.save(show);
                redirectAttributes.addFlashAttribute("successMessage", "Mot-clé ajouté !");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Ce mot-clé est déjà associé à ce spectacle.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Spectacle ou mot-clé introuvable.");
        }

        return "redirect:/shows/" + id;
    }

    @GetMapping("/shows/exclude-tag/{tag}")
    public String showsWithoutTag(@PathVariable("tag") String tagLabel, Model model) {
        Tag tag = tagService.findByTag(tagLabel).orElse(null);
        if (tag == null) {
            model.addAttribute("errorMessage", "Mot-clé non trouvé.");
            return "redirect:/shows";
        }

        List<Show> shows = service.getWithoutTag(tag);
        model.addAttribute("shows", shows);
        model.addAttribute("title", "Spectacles sans le mot-clé : " + tagLabel);

        return "show/index";
    }

    @PostMapping("/shows/{id}/reserve")
    public String reserveToCart(@PathVariable("id") String id,
                                @RequestParam Long representationId,
                                @RequestParam Long priceId,
                                @RequestParam int quantity,
                                @ModelAttribute("cart") Cart cart) {

        Representation rep = representationRepo.findById(representationId).orElse(null); // ✅
        Price price = rep.getShow().getPrices().stream()
                .filter(p -> p.getId().equals(priceId))
                .findFirst()
                .orElse(null);

        if (rep == null || price == null) {
            return "redirect:/shows/" + id + "?error";
        }

        CartItem item = new CartItem();
        item.setRepresentationId(rep.getId());
        item.setPriceId(price.getId());
        item.setQuantity(quantity);
        item.setLabel(rep.getScheduledAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        item.setUnitPrice(price.getPrice());

        cart.addItem(item);
        return "redirect:/cart/view";
    }
}
