package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Not null username!")
    @Size(min = 3,max = 20,message = "Username length must be between 3 and 20 characters!")
    private String username;
    @NotBlank(message = "Not null password!")
    @Size(min = 3,max = 20,message = "Password length must be between 3 and 20 characters!")
    private String password;
}
