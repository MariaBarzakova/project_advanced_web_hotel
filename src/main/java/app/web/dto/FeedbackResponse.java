package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class FeedbackResponse {
    private String firstName;

    private String lastName;

    private String message;

}
