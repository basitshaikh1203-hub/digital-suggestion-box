package CampusVoice.controller;

import CampusVoice.Suggestion;
import CampusVoice.SuggestionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final SuggestionRepository suggestionRepository;

    public HomeController(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/submit")
    public String submitSuggestion(
            @RequestParam("category") String category,
            @RequestParam("suggestion") String suggestion) {

        Suggestion newSuggestion =
                new Suggestion(category, suggestion);

        suggestionRepository.save(newSuggestion);

        return "redirect:/success?trackingId="
                + newSuggestion.getTrackingId();
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @GetMapping("/track")
    public String trackPage() {
        return "track";
    }

    @PostMapping("/track")
    public String trackSuggestion(
            @RequestParam("trackingId") String trackingId,
            Model model) {

        String id = trackingId.trim();

        Suggestion suggestion =
                suggestionRepository.findByTrackingId(id);

        if (suggestion != null) {
            model.addAttribute("suggestion", suggestion);
        } else {
            model.addAttribute(
                    "error",
                    "No suggestion found with this Tracking ID."
            );
        }

        return "track";
    }
}