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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.text.Collator;

@Controller
@SessionAttributes("cart")
public class ShowController {

    @ModelAttribute("cart")
    public Cart cart() { return new Cart(); }

    @Autowired ShowService service;
    @Autowired private RepresentationRepository representationRepo;
    @Autowired TagService tagService;
    @Autowired private ReviewService reviewService;

    @GetMapping({"/shows","//shows"})
    @Transactional
    public String index(@RequestParam(value = "tag", required = false) String tagLabel,
                        @RequestParam(value = "minPrice", required = false) String minPriceStr,
                        @RequestParam(value = "maxPrice", required = false) String maxPriceStr,
                        @RequestParam(value = "hideNoPrice", required = false, defaultValue = "false") boolean hideNoPrice,
                        @RequestParam(value = "repMin", required = false) Integer repMin,
                        @RequestParam(value = "repMax", required = false) Integer repMax,
                        @RequestParam(value = "sort", required = false, defaultValue = "title-asc") String sort,
                        Model model) {

        // 1) Parsing robuste
        Double minPrice = parseMoney(minPriceStr);
        Double maxPrice = parseMoney(maxPriceStr);

        // 2) Récupération
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

        // 3) Initialiser LAZY nécessaires
        for (Show s : shows) {
            Hibernate.initialize(s.getPrices());
            Hibernate.initialize(s.getRepresentations());
        }

        // 4) Pré-calcul: prix min, nb reps
        record Meta(Show show, Double minPrice, int reps) {}
        List<Meta> metas = shows.stream().map(s -> {
            Double p = null;
            if (s.getPrices() != null && !s.getPrices().isEmpty()) {
                p = s.getPrices().stream()
                        .map(Price::getPrice)
                        .filter(Objects::nonNull)
                        .filter(d -> Double.isFinite(d) && d >= 0.0)
                        .min(Double::compare)
                        .orElse(null);
            }
            int reps = (s.getRepresentations() == null) ? 0 : s.getRepresentations().size();
            return new Meta(s, p, reps);
        }).collect(Collectors.toList());

        // 5) Filtres
        Stream<Meta> stream = metas.stream();
        if (hideNoPrice || minPrice != null) {
            stream = stream.filter(m -> m.minPrice() != null);
        }
        if (minPrice != null) {
            stream = stream.filter(m -> Double.compare(m.minPrice(), minPrice) >= 0);
        }
        if (maxPrice != null) {
            // si on veut aussi exclure les "sans prix" quand maxPrice est seul, dé-commente la ligne ci-dessous:
            // stream = stream.filter(m -> m.minPrice() != null);
            stream = stream.filter(m -> m.minPrice() == null || Double.compare(m.minPrice(), maxPrice) <= 0);
        }
        if (repMin != null) stream = stream.filter(m -> m.reps() >= repMin);
        if (repMax != null) stream = stream.filter(m -> m.reps() <= repMax);

        // 6) Tri (avec tie‑breakers stables)
        Collator frCollator = Collator.getInstance(Locale.FRENCH);
        frCollator.setStrength(Collator.PRIMARY);

        Comparator<Meta> tieId = Comparator.comparingLong(m -> {
            Object id = m.show().getId();
            try {
                return Long.parseLong(String.valueOf(id));
            } catch (Exception e) {
                return String.valueOf(id).hashCode();
            }
        });

        Comparator<Meta> titleAsc = Comparator
                .comparing((Meta m) -> safeTitle(m.show()), frCollator)
                .thenComparing(tieId);

        Comparator<Meta> titleDesc = titleAsc.reversed();

        Comparator<Meta> priceAsc = Comparator
                .comparing(Meta::minPrice, Comparator.nullsLast(Double::compare))
                .thenComparing((Meta m) -> safeTitle(m.show()), frCollator)
                .thenComparing(tieId);

        Comparator<Meta> priceDesc = Comparator
                .comparing(Meta::minPrice, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing((Meta m) -> safeTitle(m.show()), frCollator)
                .thenComparing(tieId);

        Comparator<Meta> repsAsc = Comparator
                .comparingInt(Meta::reps)
                .thenComparing((Meta m) -> safeTitle(m.show()), frCollator)
                .thenComparing(tieId);

        Comparator<Meta> repsDesc = repsAsc.reversed();

        Comparator<Meta> cmp;
        switch (sort) {
            case "title-desc": cmp = titleDesc; break;
            case "price-asc":  cmp = priceAsc;  break;
            case "price-desc": cmp = priceDesc; break;
            case "rep-asc":    cmp = repsAsc;   break;
            case "rep-desc":   cmp = repsDesc;  break;
            case "title-asc":
            default:           cmp = titleAsc;  break;
        }

        List<Show> processed = stream.sorted(cmp).map(Meta::show).collect(Collectors.toList());

        // 7) Maps pour l’affichage cohérent
        Map<Object, Double> minPriceMap = new HashMap<>();
        Map<Object, Integer> repsCountMap = new HashMap<>();
        for (Meta m : metas) {
            Object showId = m.show().getId();
            minPriceMap.put(showId, m.minPrice());
            repsCountMap.put(showId, m.reps());
        }

        // 8) Modèle
        model.addAttribute("shows", processed);
        model.addAttribute("title", title);
        model.addAttribute("availableTags", tagService.findAll());
        model.addAttribute("resultCount", processed.size());
        model.addAttribute("minPriceMap", minPriceMap);
        model.addAttribute("repsCountMap", repsCountMap);

        // Réinjecter la saisie telle quelle
        model.addAttribute("filter_minPrice", minPriceStr);
        model.addAttribute("filter_maxPrice", maxPriceStr);
        model.addAttribute("filter_hideNoPrice", hideNoPrice);
        model.addAttribute("filter_repMin", repMin);
        model.addAttribute("filter_repMax", repMax);
        model.addAttribute("filter_sort", sort);

        return "show/index";
    }

    private static String safeTitle(Show s) {
        String t = (s == null) ? "" : s.getTitle();
        return (t == null) ? "" : t;
    }

    /**
     * Parse argent robuste.
     */
    private Double parseMoney(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;
        s = s.replace('\u00A0', ' ');
        s = s.replaceAll("[€\\s]", "");

        if (s.contains(",") && s.contains(".")) {
            int lastDot = s.lastIndexOf('.');
            int lastComma = s.lastIndexOf(',');
            if (lastComma > lastDot) {
                s = s.replace(".", "");
                s = s.replace(",", ".");
            } else {
                s = s.replace(",", "");
            }
        } else if (s.contains(",")) {
            s = s.replace(",", ".");
        }
        s = s.replaceAll("[^0-9.]", "");
        int firstDot = s.indexOf('.');
        if (firstDot >= 0) {
            String head = s.substring(0, firstDot + 1);
            String tail = s.substring(firstDot + 1).replace(".", "");
            s = head + tail;
        }
        if (s.isEmpty() || ".".equals(s)) return null;

        try {
            double v = Double.parseDouble(s);
            if (!Double.isFinite(v)) return null;
            return v;
        } catch (NumberFormatException e) {
            return null;
        }
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

        // minPrice pour affichage cohérent sur la fiche
        Double min = null;
        if (show.getPrices() != null && !show.getPrices().isEmpty()) {
            min = show.getPrices().stream()
                    .map(Price::getPrice)
                    .filter(Objects::nonNull)
                    .filter(d -> Double.isFinite(d) && d >= 0.0)
                    .min(Double::compare)
                    .orElse(null);
        }

        model.addAttribute("canBook", canBook);
        model.addAttribute("collaborateurs", collaborateurs);
        model.addAttribute("availableTags", tagService.findAll());
        model.addAttribute("show", show);
        model.addAttribute("title", "Fiche d'un spectacle");
        model.addAttribute("reviews", reviewService.getReviewsByShowId(show.getId()));
        model.addAttribute("minPrice", min);

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
            Set<Tag> updatedTags = new HashSet<>(show.getTags());
            if (updatedTags.add(tag)) {
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

        Representation rep = representationRepo.findById(representationId).orElse(null);
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
