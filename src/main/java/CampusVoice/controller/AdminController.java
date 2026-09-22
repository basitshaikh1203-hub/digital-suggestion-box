package CampusVoice.controller;

import CampusVoice.Suggestion;
import CampusVoice.SuggestionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminController {

    private final SuggestionRepository suggestionRepository;

    // Admin login credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public AdminController(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @GetMapping("/admin/login")
    public String loginPage(HttpSession session) {

        if (session.getAttribute("adminLoggedIn") != null) {
            return "redirect:/admin";
        }

        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (ADMIN_USERNAME.equals(username)
                && ADMIN_PASSWORD.equals(password)) {

            session.setAttribute("adminLoggedIn", true);

            return "redirect:/admin";
        }

        model.addAttribute(
                "error",
                "Invalid username or password."
        );

        return "admin-login";
    }

    @GetMapping("/admin")
    public String adminDashboard(
            HttpSession session,
            Model model) {

        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/admin/login";
        }

        List<Suggestion> suggestions =
                suggestionRepository.findAll();

        long total = suggestions.size();

        long pending = suggestions.stream()
                .filter(s ->
                        "Pending".equalsIgnoreCase(s.getStatus()))
                .count();

        long underReview = suggestions.stream()
                .filter(s ->
                        "Under Review".equalsIgnoreCase(s.getStatus()))
                .count();

        long resolved = suggestions.stream()
                .filter(s ->
                        "Resolved".equalsIgnoreCase(s.getStatus()))
                .count();

        model.addAttribute("suggestions", suggestions);
        model.addAttribute("total", total);
        model.addAttribute("pending", pending);
        model.addAttribute("underReview", underReview);
        model.addAttribute("resolved", resolved);

        return "admin";
    }

    @PostMapping("/admin/update-status")
    public String updateStatus(
            @RequestParam Long id,
            @RequestParam String status,
            HttpSession session) {

        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/admin/login";
        }

        Suggestion suggestion =
                suggestionRepository.findById(id).orElse(null);

        if (suggestion != null) {
            suggestion.setStatus(status);
            suggestionRepository.save(suggestion);
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete")
    public String deleteSuggestion(
            @RequestParam Long id,
            HttpSession session) {

        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/admin/login";
        }

        suggestionRepository.deleteById(id);

        return "redirect:/admin";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/admin/login";
    }
}