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

import java.text.Collator;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public String index(@RequestParam(value = "tag",     required = false) String tagLabel,
                        @RequestParam(value = "minPrice", required = false) String minPriceStr,
                        @RequestParam(value = "maxPrice", required = false) String maxPriceStr,
                        @RequestParam(value = "repMin",   required = false) Integer repMin,
                        @RequestParam(value = "repMax",   required = false) Integer repMax,
                        @RequestParam(value = "sort",     required = false, defaultValue = "title-asc") String sort,
                        @RequestParam(value = "page",     required = false, defaultValue = "0") int page,
                        Model model) {

        final int pageSize = 10;

        // 1) Parsing robuste
        Double minPrice = parseMoney(minPriceStr);
        Double maxPrice = parseMoney(maxPriceStr);

        // 2) Source des données
        List<Show> shows;
        String title = "Liste des spectacles";
        if (tagLabel != null && !tagLabel.isBlank()) {
            Tag tag = tagService.findByTag(tagLabel).orElse(null);
            if (tag != null) {
                shows = service.getByTag(tag);
                title += " – Mots-clés : " + tagLabel;
            } else {
                shows = new ArrayList<>();
                model.addAttribute("errorMessage", "Mot-clé introuvable");
            }
        } else {
            shows = service.getAll();
        }

        // 3) Initialiser LAZY utiles
        for (Show s : shows) {
            Hibernate.initialize(s.getPrices());
            Hibernate.initialize(s.getRepresentations());
        }

        // 4) Pré-calculs (prix plein + nb reps)
        record Meta(Show show, Double fullPrice, int reps) {}
        List<Meta> metas = shows.stream().map(s -> {
            Double p = computeFullPrice(s.getPrices());
            int reps = (s.getRepresentations() == null) ? 0 : s.getRepresentations().size();
            return new Meta(s, p, reps);
        }).collect(Collectors.toList());

        // 5) Filtres (sur PRIX PLEIN)
        Stream<Meta> stream = metas.stream();
        if (minPrice != null) stream = stream.filter(m -> m.fullPrice() != null && m.fullPrice() >= minPrice);
        if (maxPrice != null) stream = stream.filter(m -> m.fullPrice() == null || m.fullPrice() <= maxPrice);
        if (repMin   != null) stream = stream.filter(m -> m.reps() >= repMin);
        if (repMax   != null) stream = stream.filter(m -> m.reps() <= repMax);

        // 6) Tri (collation FR + tie-breaker par id) — sur PRIX PLEIN quand tri par prix
        Collator fr = Collator.getInstance(Locale.FRENCH);
        fr.setStrength(Collator.PRIMARY);

        Comparator<Meta> tieId = Comparator.comparingLong(m -> {
            Object id = m.show().getId();
            try { return Long.parseLong(String.valueOf(id)); }
            catch (Exception e) { return String.valueOf(id).hashCode(); }
        });

        Comparator<Meta> titleAsc = Comparator
                .comparing((Meta m) -> safeTitle(m.show()), fr)
                .thenComparing(tieId);
        Comparator<Meta> titleDesc = titleAsc.reversed();

        Comparator<Meta> priceAsc = Comparator
                .comparing(Meta::fullPrice, Comparator.nullsLast(Double::compare))
                .thenComparing((Meta m) -> safeTitle(m.show()), fr)
                .thenComparing(tieId);
        Comparator<Meta> priceDesc = Comparator
                .comparing(Meta::fullPrice, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing((Meta m) -> safeTitle(m.show()), fr)
                .thenComparing(tieId);

        Comparator<Meta> repsAsc = Comparator
                .comparingInt(Meta::reps)
                .thenComparing((Meta m) -> safeTitle(m.show()), fr)
                .thenComparing(tieId);
        Comparator<Meta> repsDesc = repsAsc.reversed();

        Comparator<Meta> cmp = switch (sort) {
            case "title-desc" -> titleDesc;
            case "price-asc"  -> priceAsc;
            case "price-desc" -> priceDesc;
            case "rep-asc"    -> repsAsc;
            case "rep-desc"   -> repsDesc;
            case "title-asc"  -> titleAsc;
            default           -> titleAsc;
        };

        List<Meta> filteredSorted = stream.sorted(cmp).collect(Collectors.toList());
        int total = filteredSorted.size();

        // 7) Pagination (clamp sécurisé)
        int totalPages = (int) Math.ceil((double) total / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        int fromIndex = page * pageSize;
        int toIndex   = Math.min(fromIndex + pageSize, total);
        List<Show> pageContent = (fromIndex < toIndex)
                ? filteredSorted.subList(fromIndex, toIndex).stream().map(Meta::show).collect(Collectors.toList())
                : Collections.emptyList();

        // 8) Maps pour l’affichage (PRIX PLEIN & reps)
        Map<Object, Double> fullPriceMap = new HashMap<>();
        Map<Object, Integer> repsCountMap = new HashMap<>();
        for (Meta m : filteredSorted) {
            Object showId = m.show().getId();
            fullPriceMap.put(showId, m.fullPrice());
            repsCountMap.put(showId, m.reps());
        }

        // 9) Modèle
        model.addAttribute("shows", pageContent);
        model.addAttribute("title", title);
        model.addAttribute("availableTags", tagService.findAll());
        model.addAttribute("resultCount", total);
        model.addAttribute("fullPriceMap", fullPriceMap);
        model.addAttribute("repsCountMap", repsCountMap);

        // Pagination
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);

        // Réinjecter la saisie telle quelle (pour garder les champs)
        model.addAttribute("filter_minPrice", minPriceStr);
        model.addAttribute("filter_maxPrice", maxPriceStr);
        model.addAttribute("filter_repMin", repMin);
        model.addAttribute("filter_repMax", repMax);
        model.addAttribute("filter_sort", sort);

        return "show/index";
    }

    private static String safeTitle(Show s) {
        String t = (s == null) ? "" : s.getTitle();
        return (t == null) ? "" : t;
    }

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

    /**
     * Calcule le PRIX PLEIN :
     *  - Si un prix dont le type s'appelle "plein" existe, on le renvoie.
     *  - Sinon, on prend le prix MAX (souvent le plein > étudiant).
     */
    private Double computeFullPrice(Collection<Price> prices) {
        if (prices == null || prices.isEmpty()) return null;

        // 1) chercher un prix dont le type/libellé == "plein"
        Optional<Double> plein = prices.stream()
                .filter(Objects::nonNull)
                .filter(p -> {
                    // essaie d'identifier le type "plein" de façon robuste
                    try {
                        Object typeObj = p.getType(); // ex: entity Type avec getType()
                        if (typeObj != null) {
                            try {
                                // typeObj.getType()
                                String t = (String) typeObj.getClass().getMethod("getType").invoke(typeObj);
                                if (t != null && t.trim().equalsIgnoreCase("plein")) return true;
                            } catch (Exception ignore) {}
                            try {
                                // typeObj.getLabel()
                                String t = (String) typeObj.getClass().getMethod("getLabel").invoke(typeObj);
                                if (t != null && t.toLowerCase(Locale.ROOT).contains("plein")) return true;
                            } catch (Exception ignore) {}
                        }
                    } catch (Exception ignore) {}
                    try {
                        // p.getLabel()
                        String label = (String) p.getClass().getMethod("getLabel").invoke(p);
                        if (label != null && label.toLowerCase(Locale.ROOT).contains("plein")) return true;
                    } catch (Exception ignore) {}
                    return false;
                })
                .map(Price::getPrice)
                .filter(Objects::nonNull)
                .filter(d -> Double.isFinite(d) && d >= 0.0)
                .findFirst();

        if (plein.isPresent()) return plein.get();

        // 2) fallback : prix max
        return prices.stream()
                .map(Price::getPrice)
                .filter(Objects::nonNull)
                .filter(d -> Double.isFinite(d) && d >= 0.0)
                .max(Double::compareTo)
                .orElse(null);
    }

    // ===== FICHE DÉTAIL =====
    @GetMapping("//shows/{id}")
    @Transactional
    public String show(Model model,
                       @PathVariable("id") String id,
                       // paramètres pour retour à la même page/état
                       @RequestParam(required = false) Integer page,
                       @RequestParam(required = false) String tag,
                       @RequestParam(required = false) String minPrice,
                       @RequestParam(required = false) String maxPrice,
                       @RequestParam(required = false) Integer repMin,
                       @RequestParam(required = false) Integer repMax,
                       @RequestParam(required = false, defaultValue = "title-asc") String sort) {

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

        // PRIX PLEIN pour la fiche
        Double full = computeFullPrice(show.getPrices());

        model.addAttribute("canBook", canBook);
        model.addAttribute("collaborateurs", collaborateurs);
        model.addAttribute("availableTags", tagService.findAll());
        model.addAttribute("show", show);
        model.addAttribute("title", "Fiche d'un spectacle");
        model.addAttribute("reviews", reviewService.getReviewsByShowId(show.getId()));
        model.addAttribute("computedFullPrice", full);

        // Re-propager les paramètres pour le lien "Retour" (évite d'écraser computedFullPrice)
        model.addAttribute("page", page);
        model.addAttribute("tag", tag);
        model.addAttribute("q_minPrice", minPrice);
        model.addAttribute("q_maxPrice", maxPrice);
        model.addAttribute("q_repMin", repMin);
        model.addAttribute("q_repMax", repMax);
        model.addAttribute("q_sort",   sort);

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
