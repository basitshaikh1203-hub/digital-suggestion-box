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

    private String feedbackType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String suggestion;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    private LocalDateTime createdAt;

    public Suggestion() {
    }

    public Suggestion(
            String category,
            String feedbackType,
            String suggestion) {

        this.category = category;
        this.feedbackType = feedbackType;
        this.suggestion = suggestion;
        this.status = "Pending";
        this.adminNote = "";
        this.createdAt = LocalDateTime.now();
        this.trackingId = generateTrackingId();
    }

    private String generateTrackingId() {

        String randomPart =
                UUID.randomUUID()
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

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
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

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}