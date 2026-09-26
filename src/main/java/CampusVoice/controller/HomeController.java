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

    public HomeController(
            SuggestionRepository suggestionRepository) {

        this.suggestionRepository = suggestionRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/submit")
    public String submitSuggestion(
            @RequestParam("category") String category,
            @RequestParam("feedbackType") String feedbackType,
            @RequestParam("suggestion") String suggestion,
            Model model) {

        String cleanCategory =
                category == null ? "" : category.trim();

        String cleanFeedbackType =
                feedbackType == null
                        ? ""
                        : feedbackType.trim();

        String cleanSuggestion =
                suggestion == null
                        ? ""
                        : suggestion.trim();

        if (cleanCategory.isEmpty()
                || cleanFeedbackType.isEmpty()
                || cleanSuggestion.isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please complete all required fields."
            );

            return "index";
        }

        if (cleanSuggestion.length() < 10) {

            model.addAttribute(
                    "error",
                    "Please provide a little more detail. Your suggestion should contain at least 10 characters."
            );

            return "index";
        }

        if (cleanSuggestion.length() > 1000) {

            model.addAttribute(
                    "error",
                    "Your suggestion cannot exceed 1000 characters."
            );

            return "index";
        }

        Suggestion newSuggestion =
                new Suggestion(
                        cleanCategory,
                        cleanFeedbackType,
                        cleanSuggestion
                );

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

        String id =
                trackingId == null
                        ? ""
                        : trackingId.trim().toUpperCase();

        Suggestion suggestion =
                suggestionRepository.findByTrackingId(id);

        if (suggestion != null) {

            model.addAttribute(
                    "suggestion",
                    suggestion
            );

        } else {

            model.addAttribute(
                    "error",
                    "No suggestion found with this Tracking ID."
            );
        }

        return "track";
    }
}