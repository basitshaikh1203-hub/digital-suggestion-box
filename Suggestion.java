import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Suggestion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String category;
    private String description;
    private String date;
    private String status;
    private String adminResponse;

    public Suggestion(String id, String category, String description) {
        this.id = id;
        this.category = category;
        this.description = description;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        this.date = LocalDateTime.now().format(formatter);
        this.status = "Submitted";
        this.adminResponse = "";
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }
}