package com.reservations.reservations.controller;

import java.util.List;

import com.reservations.reservations.model.Role;
import com.reservations.reservations.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class RoleController {
    @Autowired
    RoleService service;

    @GetMapping("/roles")
    public String index2(Model model) {
        List<Role> roles = service.getAll();

        model.addAttribute("roles", roles);
        model.addAttribute("title", "Liste des roles");

        return "role/index";
    }

    @GetMapping("/roles/{id}")
    public String show(Model model, @PathVariable("id") String id) {
        Role role = service.get(id);

        model.addAttribute("role", role);
        model.addAttribute("title", "Fiche d'un role");

        return "role/show";
    }

}