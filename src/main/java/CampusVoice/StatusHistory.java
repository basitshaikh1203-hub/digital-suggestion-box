package CampusVoice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long suggestionId;

    private String trackingId;

    private String oldStatus;

    private String newStatus;

    private LocalDateTime changedAt;

    public StatusHistory() {
    }

    public StatusHistory(
            Long suggestionId,
            String trackingId,
            String oldStatus,
            String newStatus) {

        this.suggestionId = suggestionId;
        this.trackingId = trackingId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getSuggestionId() {
        return suggestionId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}