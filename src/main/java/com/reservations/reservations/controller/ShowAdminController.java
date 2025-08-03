package com.reservations.reservations.controller;

import com.reservations.reservations.dto.CollabForm;
import com.reservations.reservations.dto.RepForm;
import com.reservations.reservations.dto.ShowForm;
import com.reservations.reservations.model.Price;
import com.reservations.reservations.model.Representation;
import com.reservations.reservations.model.Show;
import com.reservations.reservations.model.Tag;
import com.reservations.reservations.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ShowAdminController {

    @Autowired
    private ShowService showService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ArtistService artistService;
    @Autowired
    private ArtistTypeService artistTypeService;

    private void populateFormLists(Model m) {
        m.addAttribute("locations", locationService.getAll());
        m.addAttribute("tags", tagService.findAll());
        m.addAttribute("artists", artistService.getAllArtists());
        m.addAttribute("types", artistTypeService.getAllTypes());
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("shows", showService.getAll());
        return "admin/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        ShowForm form = new ShowForm();
        form.getRepresentations().add(new RepForm());
        form.getCollaborators().add(new CollabForm());
        model.addAttribute("showForm", form);
        populateFormLists(model);
        return "admin/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("showForm") ShowForm form,
                         BindingResult br,
                         RedirectAttributes ra,
                         Model model) {
        if (br.hasErrors()) {
            populateFormLists(model);
            return "admin/shows/form";
        }
        Show s = buildShowFromForm(form);
        showService.add(s);
        ra.addFlashAttribute("success", "Spectacle créé !");
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Show s = showService.get(id.toString());
        ShowForm form = toForm(s);
        form.getRepresentations().forEach(r -> {/* rien */});
        form.getCollaborators().forEach(c -> {/* rien */});
        model.addAttribute("showForm", form);
        model.addAttribute("editingId", id);
        populateFormLists(model);
        return "admin/shows/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("showForm") ShowForm form,
                         BindingResult br,
                         RedirectAttributes ra,
                         Model model) {
        if (br.hasErrors()) {
            populateFormLists(model);
            return "admin/shows/form";
        }
        Show s = buildShowFromForm(form);
        s.setId(id);
        showService.update(id.toString(), s);
        ra.addFlashAttribute("success", "Spectacle mis à jour !");
        return "redirect:/admin/shows";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        showService.delete(id.toString());
        ra.addFlashAttribute("success", "Spectacle supprimé !");
        return "redirect:/admin/shows";
    }

    private Show buildShowFromForm(ShowForm form) {
        Show s = new Show();
        s.setTitle(form.getTitle());
        s.setDescription(form.getDescription());
        s.setPosterUrl(form.getPosterUrl());
        s.setBookable(form.isBookable());
        s.setLocation(locationService.get(form.getLocationId().toString()));

        // --- Prix plein ---
        Price plein = new Price();
        plein.setType("Plein");
        plein.setPrice(form.getFullPrice());
        plein.setStartDate(form.getFullPriceStartDate());
        plein.setEndDate(form.getFullPriceEndDate());
        priceService.addPrice(plein);
        s.getPrices().add(plein);



        // --- Prix réduit éventuel ---
        if (form.getReducedPrice() != null) {
            Price reduit = new Price();
            reduit.setType("Réduit");
            reduit.setPrice(form.getReducedPrice());
            reduit.setStartDate(form.getReducedPriceStartDate());
            reduit.setEndDate(form.getReducedPriceEndDate());
            priceService.addPrice(reduit);
            s.getPrices().add(reduit);
        }

        // — Tags existants + nouveaux mots-clés —
        form.getTagIds().stream()
                .map(tagService::find)
                .forEach(opt -> opt.ifPresent(s.getTags()::add));
        if (form.getNewTags() != null) {
            form.getNewTags().stream()
                    .filter(t -> t != null && !t.isBlank())
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .forEach(label -> {
                        var tag = tagService.findByTag(label)
                                .orElseGet(() -> {
                                    var nt = new Tag();
                                    nt.setTag(label);
                                    tagService.save(nt);
                                    return nt;
                                });
                        s.getTags().add(tag);
                    });
        }

        // — Représentations —
        form.getRepresentations().forEach(rf -> {
            Representation r = new Representation();
            r.setScheduledAt(rf.getScheduledAt());
            r.setCapacity(rf.getCapacity());
            r.setLocation(locationService.get(rf.getLocationId().toString()));
            s.addRepresentation(r);
        });

        // — Collaborateurs —
        form.getCollaborators().forEach(cf -> {
            var artist = artistService.getArtist(cf.getArtistId());
            var at = artistTypeService.getOrCreate(artist, cf.getTypeId());
            s.addArtistType(at);
        });

        return s;
    }

    private ShowForm toForm(Show s) {
        ShowForm form = new ShowForm();
        form.setTitle(s.getTitle());
        form.setDescription(s.getDescription());
        form.setPosterUrl(s.getPosterUrl());
        form.setBookable(s.isBookable());
        form.setLocationId(s.getLocation().getId());

        // On cherche et on remplit d’abord le tarif Plein puis le Réduit
        s.getPrices().stream()
                .filter(p -> "Plein".equals(p.getType()))
                .findFirst()
                .ifPresent(p -> {
                    form.setFullPrice(p.getPrice());
                    form.setFullPriceStartDate(p.getStartDate());
                    form.setFullPriceEndDate(p.getEndDate());
                });

        s.getPrices().stream()
                .filter(p -> "Réduit".equals(p.getType()))
                .findFirst()
                .ifPresent(p -> {
                    form.setReducedPrice(p.getPrice());
                    form.setReducedPriceStartDate(p.getStartDate());
                    form.setReducedPriceEndDate(p.getEndDate());
                });

        s.getTags().forEach(t -> form.getTagIds().add(t.getId()));

        s.getRepresentations().forEach(r -> {
            RepForm rf = new RepForm();
            rf.setScheduledAt(r.getScheduledAt());
            rf.setCapacity(r.getCapacity());
            rf.setLocationId(r.getLocation().getId());
            form.getRepresentations().add(rf);
        });

        s.getArtistTypes().forEach(at -> {
            CollabForm cf = new CollabForm();
            cf.setArtistId(at.getArtist().getId());
            cf.setTypeId(at.getType().getId());
            form.getCollaborators().add(cf);
        });

        return form;
    }
}