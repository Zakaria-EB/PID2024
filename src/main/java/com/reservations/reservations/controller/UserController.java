package com.reservations.reservations.controller;


import com.reservations.reservations.model.User;
import com.reservations.reservations.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Controller
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/users/{id}/edit")
    public String edit(@PathVariable String id, Model model, HttpServletRequest request) {

        User user = service.getUser(id);
        model.addAttribute("user", user);

        String referrer = request.getHeader("Referer");
        model.addAttribute("back", (referrer != null && !referrer.isBlank()) ? referrer : "/users/" + id);

        return "user/edit";
    }

    public String update(@PathVariable String id, @Valid @ModelAttribute User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        service.updateUser(id, user);
        return "redirect:/users/" + id; // redirige vers le profil ou autre page pertinente
    }



}
