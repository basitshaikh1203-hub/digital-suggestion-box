package CampusVoice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "suggestions")
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String trackingId;

    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String suggestion;

    private String status;

    private LocalDateTime createdAt;

    public Suggestion() {
    }

    public Suggestion(String category, String suggestion) {
        this.category = category;
        this.suggestion = suggestion;
        this.status = "Pending";
        this.createdAt = LocalDateTime.now();
        this.trackingId = generateTrackingId();
    }

    private String generateTrackingId() {
        String randomPart = UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();

        return "CV-" + randomPart;
    }

    public Long getId() {
        return id;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
