package CampusVoice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    Suggestion findByTrackingId(String trackingId);
    List<Suggestion> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);
}
