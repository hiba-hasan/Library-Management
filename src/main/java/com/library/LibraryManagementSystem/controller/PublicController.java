package com.library.LibraryManagementSystem.controller;

import com.library.LibraryManagementSystem.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/")
    public String home() {
        return "index"; // Renders index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Renders login.html
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // Renders register.html
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String name,
                                      @RequestParam String email,
                                      @RequestParam String password) {
        try {
            memberService.registerNewMember(name, email, password);
            return "redirect:/login?register_success";
        } catch (RuntimeException ex) {
            return "redirect:/register?error=" + ex.getMessage();
        }
    }

    // This single dashboard endpoint redirects users to the correct dashboard
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        // Check the user's role and redirect
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (auth.getAuthority().equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
            if (auth.getAuthority().equals("ROLE_MEMBER")) {
                return "redirect:/member/dashboard";
            }
        }
        // Fallback
        return "redirect:/login";
    }
}