package com.devops.employee.controller;

import com.devops.employee.model.User;
import com.devops.employee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    private String getLoggedInUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    @GetMapping
    public String viewProfile(Model model) {
        String username = getLoggedInUsername();
        if (username == null || username.equals("anonymousUser")) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "profile/view";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("email") String email,
                                Model model) {
        String username = getLoggedInUsername();
        if (username == null) {
            return "redirect:/login";
        }
        try {
            userService.updateProfile(username, firstName, lastName, email);
            return "redirect:/profile?updateSuccess";
        } catch (IllegalArgumentException e) {
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage());
            return "profile/view";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        String username = getLoggedInUsername();
        if (username == null) {
            return "redirect:/login";
        }
        try {
            userService.changePassword(username, oldPassword, newPassword);
            return "redirect:/profile?passwordSuccess";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "profile/change-password";
        }
    }
}
