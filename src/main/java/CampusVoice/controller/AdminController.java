package CampusVoice.controller;

import CampusVoice.StatusHistory;
import CampusVoice.StatusHistoryRepository;
import CampusVoice.Suggestion;
import CampusVoice.SuggestionRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final SuggestionRepository suggestionRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    @Value("${campusvoice.admin.username}")
    private String adminUsername;

    @Value("${campusvoice.admin.password}")
    private String adminPassword;

    public AdminController(
            SuggestionRepository suggestionRepository,
            StatusHistoryRepository statusHistoryRepository) {

        this.suggestionRepository = suggestionRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (adminUsername.equals(username)
                && adminPassword.equals(password)) {

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

        if (!Boolean.TRUE.equals(
                session.getAttribute("adminLoggedIn"))) {

            return "redirect:/admin/login";
        }

        List<Suggestion> suggestions =
                suggestionRepository.findAll();

        long total =
                suggestions.size();

        long pending =
                suggestions.stream()
                        .filter(s ->
                                "Pending".equalsIgnoreCase(
                                        s.getStatus()))
                        .count();

        long underReview =
                suggestions.stream()
                        .filter(s ->
                                "Under Review".equalsIgnoreCase(
                                        s.getStatus()))
                        .count();

        long resolved =
                suggestions.stream()
                        .filter(s ->
                                "Resolved".equalsIgnoreCase(
                                        s.getStatus()))
                        .count();

        Map<String, Long> categoryCounts =
                suggestions.stream()
                        .collect(Collectors.groupingBy(
                                Suggestion::getCategory,
                                LinkedHashMap::new,
                                Collectors.counting()
                        ));

        String[] categories = {
                "Academics",
                "Infrastructure",
                "Canteen",
                "Transport",
                "Events",
                "Other"
        };

        for (String category : categories) {
            categoryCounts.putIfAbsent(
                    category,
                    0L
            );
        }

        Map<String, Long> statusCounts =
                new LinkedHashMap<>();

        statusCounts.put(
                "Pending",
                pending
        );

        statusCounts.put(
                "Under Review",
                underReview
        );

        statusCounts.put(
                "Resolved",
                resolved
        );

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("dd MMM");

        Map<LocalDate, Long> dateCounts =
                suggestions.stream()
                        .filter(s ->
                                s.getCreatedAt() != null)
                        .collect(Collectors.groupingBy(
                                s -> s.getCreatedAt()
                                        .toLocalDate(),
                                Collectors.counting()
                        ));

        Map<String, Long> dailyCounts =
                new LinkedHashMap<>();

        dateCounts.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        dailyCounts.put(
                                entry.getKey()
                                        .format(dateFormatter),
                                entry.getValue()
                        )
                );

        model.addAttribute(
                "suggestions",
                suggestions
        );

        model.addAttribute(
                "total",
                total
        );

        model.addAttribute(
                "pending",
                pending
        );

        model.addAttribute(
                "underReview",
                underReview
        );

        model.addAttribute(
                "resolved",
                resolved
        );

        model.addAttribute(
                "categoryCounts",
                categoryCounts
        );

        model.addAttribute(
                "statusCounts",
                statusCounts
        );

        model.addAttribute(
                "dailyCounts",
                dailyCounts
        );

        return "admin";
    }

    @PostMapping("/admin/update")
    public String updateSuggestion(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String adminNote,
            HttpSession session) {

        if (!Boolean.TRUE.equals(
                session.getAttribute("adminLoggedIn"))) {

            return "redirect:/admin/login";
        }

        Suggestion suggestion =
                suggestionRepository.findById(id)
                        .orElse(null);

        if (suggestion != null) {

            String oldStatus =
                    suggestion.getStatus();

            if (oldStatus == null) {
                oldStatus = "Pending";
            }

            if (!oldStatus.equalsIgnoreCase(status)) {

                StatusHistory history =
                        new StatusHistory(
                                suggestion.getId(),
                                suggestion.getTrackingId(),
                                oldStatus,
                                status
                        );

                statusHistoryRepository.save(history);
            }

            suggestion.setStatus(status);

            if (adminNote != null) {

                suggestion.setAdminNote(
                        adminNote.trim()
                );
            }

            suggestionRepository.save(suggestion);
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/history")
    public String statusHistory(
            @RequestParam Long id,
            HttpSession session,
            Model model) {

        if (!Boolean.TRUE.equals(
                session.getAttribute("adminLoggedIn"))) {

            return "redirect:/admin/login";
        }

        Suggestion suggestion =
                suggestionRepository.findById(id)
                        .orElse(null);

        if (suggestion == null) {
            return "redirect:/admin";
        }

        List<StatusHistory> history =
                statusHistoryRepository
                        .findBySuggestionIdOrderByChangedAtDesc(id);

        model.addAttribute(
                "suggestion",
                suggestion
        );

        model.addAttribute(
                "history",
                history
        );

        return "status-history";
    }

    @PostMapping("/admin/delete")
    public String deleteSuggestion(
            @RequestParam Long id,
            HttpSession session) {

        if (!Boolean.TRUE.equals(
                session.getAttribute("adminLoggedIn"))) {

            return "redirect:/admin/login";
        }

        List<StatusHistory> history =
                statusHistoryRepository
                        .findBySuggestionIdOrderByChangedAtDesc(id);

        statusHistoryRepository.deleteAll(history);

        suggestionRepository.deleteById(id);

        return "redirect:/admin";
    }

    @GetMapping("/admin/logout")
    public String logout(
            HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}