package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEditRequest {
    @NotBlank
    @Size(min = 3, max = 20, message = "First name should be between 3 and 20 symbols")
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 20, message = "Last name should be between 3 and 20 symbols")
    private String lastName;

    @NotBlank(message = "Requires correct phone number for contact")
    private String phoneNumber;

    @NotBlank(message = "Requires correct address for correspondence")
    private String address;

    @NotBlank(message = "Requires correct passport number")
    private String passport;

    private Boolean active;
}
