package com.reservations.reservations.controller;

import java.util.List;

import com.reservations.reservations.model.Locality;
import com.reservations.reservations.service.LocalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class LocalityController {
    @Autowired
    LocalityService service;

    @GetMapping("/localities")
    public String index2(Model model) {
        List<Locality> localities = service.getAll();

        model.addAttribute("localities", localities);
        model.addAttribute("title", "Liste des localités");

        return "locality/index";
    }

    @GetMapping("/localities/{id}")
    public String show(Model model, @PathVariable("id") String id) {
        Locality locality = service.get(id);

        model.addAttribute("locality", locality);
        model.addAttribute("title", "Fiche d'une localité");

        return "locality/show";
    }

}