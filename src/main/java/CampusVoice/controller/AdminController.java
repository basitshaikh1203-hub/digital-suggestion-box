package CampusVoice.controller;

import CampusVoice.AdminAccount;
import CampusVoice.AdminAccountRepository;
import CampusVoice.PasswordHasher;
import CampusVoice.StatusHistory;
import CampusVoice.StatusHistoryRepository;
import CampusVoice.Suggestion;
import CampusVoice.SuggestionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private static final List<String> CATEGORIES = List.of(
            "Academics", "Infrastructure", "Canteen", "Transport", "Events", "Other");
    private static final List<String> STATUSES = List.of("Pending", "Under Review", "Resolved");

    private final SuggestionRepository suggestionRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final AdminAccountRepository adminAccountRepository;
    private final PasswordHasher passwordHasher;

    public AdminController(SuggestionRepository suggestionRepository,
                           StatusHistoryRepository statusHistoryRepository,
                           AdminAccountRepository adminAccountRepository,
                           PasswordHasher passwordHasher) {
        this.suggestionRepository = suggestionRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.adminAccountRepository = adminAccountRepository;
        this.passwordHasher = passwordHasher;
    }

    @GetMapping("/admin/login")
    public String loginPage(HttpSession session) {
        if (currentAdmin(session).isPresent()) return "redirect:/admin";
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        Model model) {
        Optional<AdminAccount> match = adminAccountRepository.findByUsernameIgnoreCase(
                username == null ? "" : username.trim());

        if (match.isPresent() && match.get().isEnabled()
                && passwordHasher.matches(password == null ? "" : password,
                                          match.get().getPasswordHash())) {
            HttpSession session = request.getSession(true);
            request.changeSessionId();
            session.setAttribute("adminAccountId", match.get().getId());
            return "redirect:/admin";
        }

        model.addAttribute("error", "Invalid username or password, or this account is disabled.");
        return "admin-login";
    }

    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        Optional<AdminAccount> loggedIn = currentAdmin(session);
        if (loggedIn.isEmpty()) return "redirect:/admin/login";

        AdminAccount admin = loggedIn.get();
        List<Suggestion> suggestions = getVisibleSuggestions(admin);
        long total = suggestions.size();
        long pending = countStatus(suggestions, "Pending");
        long underReview = countStatus(suggestions, "Under Review");
        long resolved = countStatus(suggestions, "Resolved");

        Map<String, Long> categoryCounts = new LinkedHashMap<>();
        if (admin.isHod()) {
            Map<String, Long> grouped = suggestions.stream().collect(Collectors.groupingBy(
                    Suggestion::getCategory, LinkedHashMap::new, Collectors.counting()));
            for (String category : CATEGORIES) categoryCounts.put(category, grouped.getOrDefault(category, 0L));
        } else {
            categoryCounts.put(admin.getCategory(), total);
        }

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        statusCounts.put("Pending", pending);
        statusCounts.put("Under Review", underReview);
        statusCounts.put("Resolved", resolved);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");
        Map<LocalDate, Long> dateCounts = suggestions.stream()
                .filter(s -> s.getCreatedAt() != null)
                .collect(Collectors.groupingBy(s -> s.getCreatedAt().toLocalDate(), Collectors.counting()));
        Map<String, Long> dailyCounts = new LinkedHashMap<>();
        dateCounts.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry ->
                dailyCounts.put(entry.getKey().format(dateFormatter), entry.getValue()));

        model.addAttribute("admin", admin);
        model.addAttribute("isHod", admin.isHod());
        model.addAttribute("suggestions", suggestions);
        model.addAttribute("total", total);
        model.addAttribute("pending", pending);
        model.addAttribute("underReview", underReview);
        model.addAttribute("resolved", resolved);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("dailyCounts", dailyCounts);
        return "admin";
    }

    @GetMapping("/admin/users")
    public String manageAdmins(HttpSession session, Model model) {
        Optional<AdminAccount> loggedIn = currentAdmin(session);
        if (loggedIn.isEmpty()) return "redirect:/admin/login";
        if (!loggedIn.get().isHod()) return "redirect:/admin";

        model.addAttribute("admin", loggedIn.get());
        model.addAttribute("accounts", adminAccountRepository.findAllByOrderByRoleAscDisplayNameAsc());
        model.addAttribute("categories", CATEGORIES);
        return "admin-users";
    }

    @PostMapping("/admin/users/create")
    public String createAdmin(@RequestParam("displayName") String displayName,
                              @RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("category") String category,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Optional<AdminAccount> loggedIn = currentAdmin(session);
        if (loggedIn.isEmpty()) return "redirect:/admin/login";
        if (!loggedIn.get().isHod()) return "redirect:/admin";

        String cleanName = clean(displayName);
        String cleanUsername = clean(username);
        String cleanCategory = clean(category);
        String cleanPassword = password == null ? "" : password;

        if (cleanName.isBlank() || cleanUsername.isBlank() || !CATEGORIES.contains(cleanCategory)) {
            redirectAttributes.addFlashAttribute("error", "Complete all fields and choose a valid category.");
            return "redirect:/admin/users";
        }
        if (!cleanUsername.matches("[A-Za-z0-9._-]{3,80}")) {
            redirectAttributes.addFlashAttribute("error", "Username must be 3-80 characters and use only letters, numbers, dots, underscores, or hyphens.");
            return "redirect:/admin/users";
        }
        if (cleanPassword.length() < 10) {
            redirectAttributes.addFlashAttribute("error", "Use a password with at least 10 characters.");
            return "redirect:/admin/users";
        }
        if (adminAccountRepository.existsByUsernameIgnoreCase(cleanUsername)) {
            redirectAttributes.addFlashAttribute("error", "That username is already in use.");
            return "redirect:/admin/users";
        }
        if (categoryAlreadyAssigned(cleanCategory, null)) {
            redirectAttributes.addFlashAttribute("error", "An active admin is already assigned to " + cleanCategory + ". Reassign or delete that account first.");
            return "redirect:/admin/users";
        }

        AdminAccount account = new AdminAccount();
        account.setDisplayName(cleanName);
        account.setUsername(cleanUsername);
        account.setPasswordHash(passwordHasher.hash(cleanPassword));
        account.setRole(AdminAccount.ROLE_CATEGORY_ADMIN);
        account.setCategory(cleanCategory);
        account.setEnabled(true);
        adminAccountRepository.save(account);
        redirectAttributes.addFlashAttribute("success", "Category admin created successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/update")
    public String updateAdmin(@RequestParam("id") Long id,
                              @RequestParam("displayName") String displayName,
                              @RequestParam("category") String category,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Optional<AdminAccount> loggedIn = currentAdmin(session);
        if (loggedIn.isEmpty()) return "redirect:/admin/login";
        if (!loggedIn.get().isHod()) return "redirect:/admin";

        AdminAccount account = adminAccountRepository.findById(id).orElse(null);
        if (account == null || account.isHod()) {
            redirectAttributes.addFlashAttribute("error", "That category-admin account cannot be updated.");
            return "redirect:/admin/users";
        }
        String cleanName = clean(displayName);
        String cleanCategory = clean(category);
        if (cleanName.isBlank() || !CATEGORIES.contains(cleanCategory)) {
            redirectAttributes.addFlashAttribute("error", "Enter a display name and choose a valid category.");
            return "redirect:/admin/users";
        }
        if (categoryAlreadyAssigned(cleanCategory, account.getId())) {
            redirectAttributes.addFlashAttribute("error", "Another active admin is already assigned to " + cleanCategory + ".");
            return "redirect:/admin/users";
        }
        account.setDisplayName(cleanName);
        account.setCategory(cleanCategory);
        adminAccountRepository.save(account);
        redirectAttributes.addFlashAttribute("success", "Admin details and category assignment updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/delete")
    public String deleteAdmin(@RequestParam("id") Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Optional<AdminAccount> loggedIn = currentAdmin(session);
        if (loggedIn.isEmpty()) return "redirect:/admin/login";
        if (!loggedIn.get().isHod()) return "redirect:/admin";

        AdminAccount account = adminAccountRepository.findById(id).orElse(null);
        if (account == null || account.isHod()) {
            redirectAttributes.addFlashAttribute("error", "The HOD account cannot be deleted.");
            return "redirect:/admin/users";
        }
        adminAccountRepository.delete(account);
        redirectAttributes.addFlashAttribute("success", "Admin account deleted. Existing suggestions were not deleted.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/update")
    public String updateSuggestion(@RequestParam("id") Long id,
                                  @RequestParam("status") String status,
                                  @RequestParam(value = "adminNote", required = false) String adminNote,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Optional<AdminAccount> loggedIn = currentAdmin(session);
        if (loggedIn.isEmpty()) return "redirect:/admin/login";
        if (!STATUSES.contains(status)) {
            redirectAttributes.addFlashAttribute("error", "Invalid suggestion status.");
            return "redirect:/admin";
        }

        Suggestion suggestion = findVisibleSuggestion(id, loggedIn.get());
        if (suggestion == null) return "redirect:/admin";

        String oldStatus = suggestion.getStatus() == null ? "Pending" : suggestion.getStatus();
        if (!oldStatus.equalsIgnoreCase(status)) {
            statusHistoryRepository.save(new StatusHistory(
                    suggestion.getId(), suggestion.getTrackingId(), oldStatus, status));
        }
        suggestion.setStatus(status);
        if (adminNote != null) suggestion.setAdminNote(adminNote.trim());
        suggestionRepository.save(suggestion);
        return "redirect:/admin";
    }

    @GetMapping("/admin/history")
    public String statusHistory(@RequestParam("id") Long id,
                                HttpSession session,
                                Model model) {
        Optional<AdminAccount> loggedIn = currentAdmin(session);
        if (loggedIn.isEmpty()) return "redirect:/admin/login";
        Suggestion suggestion = findVisibleSuggestion(id, loggedIn.get());
        if (suggestion == null) return "redirect:/admin";

        List<StatusHistory> history = statusHistoryRepository
                .findBySuggestionIdOrderByChangedAtDesc(id);
        model.addAttribute("admin", loggedIn.get());
        model.addAttribute("suggestion", suggestion);
        model.addAttribute("history", history);
        return "status-history";
    }

    @PostMapping("/admin/delete")
    public String deleteSuggestion(@RequestParam("id") Long id,
                                   HttpSession session) {
        Optional<AdminAccount> loggedIn = currentAdmin(session);
        if (loggedIn.isEmpty()) return "redirect:/admin/login";
        Suggestion suggestion = findVisibleSuggestion(id, loggedIn.get());
        if (suggestion == null) return "redirect:/admin";

        List<StatusHistory> history = statusHistoryRepository
                .findBySuggestionIdOrderByChangedAtDesc(id);
        statusHistoryRepository.deleteAll(history);
        suggestionRepository.delete(suggestion);
        return "redirect:/admin";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private Optional<AdminAccount> currentAdmin(HttpSession session) {
        Object id = session.getAttribute("adminAccountId");
        if (!(id instanceof Long adminId)) return Optional.empty();
        Optional<AdminAccount> account = adminAccountRepository.findById(adminId);
        if (account.isEmpty() || !account.get().isEnabled()) {
            session.removeAttribute("adminAccountId");
            return Optional.empty();
        }
        return account;
    }

    private List<Suggestion> getVisibleSuggestions(AdminAccount admin) {
        if (admin.isHod()) return suggestionRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(
                        Suggestion::getCreatedAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .toList();
        if (admin.getCategory() == null) return new ArrayList<>();
        return suggestionRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(admin.getCategory());
    }

    private Suggestion findVisibleSuggestion(Long id, AdminAccount admin) {
        Suggestion suggestion = suggestionRepository.findById(id).orElse(null);
        if (suggestion == null) return null;
        if (admin.isHod()) return suggestion;
        return admin.getCategory() != null
                && admin.getCategory().equalsIgnoreCase(suggestion.getCategory())
                ? suggestion : null;
    }

    private boolean categoryAlreadyAssigned(String category, Long exceptId) {
        return adminAccountRepository.findAll().stream()
                .anyMatch(account -> account.isEnabled()
                        && AdminAccount.ROLE_CATEGORY_ADMIN.equals(account.getRole())
                        && category.equalsIgnoreCase(account.getCategory())
                        && (exceptId == null || !exceptId.equals(account.getId())));
    }

    private long countStatus(List<Suggestion> suggestions, String status) {
        return suggestions.stream().filter(s -> status.equalsIgnoreCase(s.getStatus())).count();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
