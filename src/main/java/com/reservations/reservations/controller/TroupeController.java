package com.reservations.reservations.controller;

import com.reservations.reservations.model.Troupe;
import com.reservations.reservations.repository.TroupeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/troupes")
public class TroupeController {

    private final TroupeRepository troupeRepository;

    public TroupeController(TroupeRepository troupeRepository) {
        this.troupeRepository = troupeRepository;
    }

    @GetMapping
    public String listTroupes(Model model) {
        model.addAttribute("troupes", troupeRepository.findAll());
        return "troupes/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("troupe", new Troupe());
        return "troupes/create";
    }

    @PostMapping
    public String createTroupe(@ModelAttribute Troupe troupe) {
        if (!troupeRepository.existsByName(troupe.getName())) {
            troupeRepository.save(troupe);
        }
        return "redirect:/troupes";
    }
}