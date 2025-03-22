package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class FeedbackResponse {
    private String firstName;

    private String lastName;

    private String message;

    private LocalDateTime createdOn;
}
