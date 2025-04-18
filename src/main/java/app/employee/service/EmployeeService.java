package app.employee.service;

import app.employee.client.EmployeeClient;
import app.employee.client.dto.Employee;
import app.employee.client.dto.EmployeeRequest;
import app.exception.EmployeeFeignException;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EmployeeService {
    private final EmployeeClient employeeClient;
    private final UserRepository userRepository;

    @Autowired
    public EmployeeService(EmployeeClient employeeClient, UserRepository userRepository) {
        this.employeeClient = employeeClient;
        this.userRepository = userRepository;
    }

    public void createEmployeeRequest(UUID userId) {
        User employee = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getRole() != UserRole.EMPLOYEE) {
            log.warn("User with ID [%s] is not an employee.".formatted(userId));
            return;
        }
        EmployeeRequest employeeRequest = EmployeeRequest.builder()
                .userId(employee.getId())
                .username(employee.getUsername())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .phoneNumber(employee.getPhoneNumber())
                .email(employee.getEmail())
                .address(employee.getAddress())
                .passport(employee.getPassport())
                .role("EMPLOYEE")
                .active(true)
                .build();

        try {
            ResponseEntity<Void> httpResponse = employeeClient.upsertEmployee(employeeRequest);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to save employee with id [%s]".formatted(employee.getId()));
            }
        } catch (Exception e) {
            log.error("Error calling employee-svc");
        }
    }

    public List<Employee> getAllEmployees() {
        ResponseEntity<List<Employee>> httpResponse = employeeClient.getAllEmployees();
        return httpResponse.getBody();
    }


    public Employee getEmployeeByUserId(UUID userId) {
        ResponseEntity<Employee> httpResponse = employeeClient.getEmployeeByUserId(userId);
        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Employee is not available");
        }
        return httpResponse.getBody();
    }

    public void updateStatus(UUID userId) {
        try {
            employeeClient.updateStatus(userId);
        } catch (Exception e) {
            log.error("Unable to call employee-svc to change active to false.");
            throw new RuntimeException("Employee service is temporary down!Please try again later!");
        }
    }
}
