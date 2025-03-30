package app.employee.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class EmployeeRequest {
    private UUID userId;

    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String address;

    private String passport;

    private String role;

    private Boolean active;
}
