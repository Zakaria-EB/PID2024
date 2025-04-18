package com.reservations.reservations.controller;

import com.reservations.reservations.model.Show;
import com.reservations.reservations.model.Artist;
import com.reservations.reservations.model.ArtistType;
import com.reservations.reservations.service.ShowService;
import com.reservations.reservations.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.TreeMap;

@Controller
public class ShowController {

    @Autowired
    private ShowService service;
    @Autowired
    private TagService tagService;

    // Méthode pour afficher la liste des spectacles
   /* @GetMapping("/shows")
    public String index(Model model) {
        List<Show> shows = service.getAllShows(); // Appel à getAll()

        model.addAttribute("shows", shows);
        model.addAttribute("title", "Liste des spectacles");

        return "show/index";
    }*/

    // Méthode pour afficher un spectacle en particulier
    @GetMapping("/shows/{id}")
    public String show(Model model, @PathVariable("id") String id) {
        // On récupère l'objet Show, et on vérifie si il existe
        Show show = (Show) service.getShowById(Long.valueOf(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Spectacle non trouvé"));
        //Récupérer les artistes du spectacle et les grouper par type
        Map<String,ArrayList<Artist>> collaborateurs = new TreeMap<>();

        for(ArtistType at : show.getArtistTypes()) {
            String type = at.getType().getType();

            if(collaborateurs.get(type) == null) {
                collaborateurs.put(type, new ArrayList<>());
            }

            collaborateurs.get(type).add(at.getArtist());
        }

        model.addAttribute("collaborateurs", collaborateurs);
        model.addAttribute("show", show);
        model.addAttribute("title", "Fiche d'un spectacle");

        return "show/show";
    }

    @GetMapping("/shows")
    public String index(Model model,
                        @RequestParam(name = "search", required = false) String search) {
        List<Show> shows;

        if (search != null && !search.isEmpty()) {
            shows = service.searchByTagKeyword(search);
            model.addAttribute("search", search);
            model.addAttribute("resultsCount", shows.size());
        } else {
            shows = service.getAllShows();
        }

        model.addAttribute("shows", shows);
        model.addAttribute("title", "Liste des spectacles");

        return "show/index"; // ton template doit avoir un champ de recherche !
    }

    @GetMapping("/shows/exclude-tag/{tag}")
    public String excludeByTag(@PathVariable String tag, Model model) {
        List<Show> shows = service.excludeByTag(tag);

        model.addAttribute("shows", shows);
        model.addAttribute("excludedTag", tag);
        model.addAttribute("title", "Spectacles sans le mot-clé : " + tag);

        return "show/index"; // réutilise la même vue
    }

    @PostMapping("/shows/{id}/add-tag")
    @PreAuthorize("hasRole('ADMIN')")
    public String addTag(@PathVariable Long id,
                         @RequestParam String tag,
                         RedirectAttributes redirectAttributes) {
        Show show = service.getShowById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tagService.addTagToShow(tag, show); // Tu dois créer cette méthode dans le service
        redirectAttributes.addFlashAttribute("message", "Mot-clé ajouté !");
        return "redirect:/shows/" + id;
    }



}
