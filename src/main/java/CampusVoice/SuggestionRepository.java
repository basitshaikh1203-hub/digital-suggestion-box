package CampusVoice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    Suggestion findByTrackingId(String trackingId);
}