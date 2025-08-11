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
    @PutMapping("/users/{id}/edit")
    public String update(@PathVariable String id, @Valid @ModelAttribute User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        User existing = service.getUser(id);
        if (existing == null) {
            return "redirect:/users";
        }
            existing.setLogin(user.getLogin());
            existing.setFirstname(user.getFirstname());
            existing.setLastname(user.getLastname());
            existing.setEmail(user.getEmail());
            existing.setLangue(user.getLangue());

        service.updateUser(id, existing);
        return "redirect:/users/" + id;
    }

    @GetMapping("/users/{id}")
    public String showUser(@PathVariable String id, Model model) {
        User user = service.getUser(id);
        model.addAttribute("user", user);
        return "user/show";
    }

}
