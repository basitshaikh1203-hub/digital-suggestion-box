package CampusVoice.controller;

import CampusVoice.Suggestion;
import CampusVoice.SuggestionRepository;
import org.springframework.stereotype.Controller;
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
            @RequestParam String category,
            @RequestParam String suggestion) {

        Suggestion newSuggestion = new Suggestion(category, suggestion);

        suggestionRepository.save(newSuggestion);

        return "redirect:/?success=true";
    }
}