package app.unitTests;

import app.employee.client.EmployeeClient;
import app.employee.client.dto.Employee;
import app.employee.client.dto.EmployeeRequest;
import app.employee.service.EmployeeService;
import app.exception.EmployeeFeignException;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceUTest {
    @Mock
    private EmployeeClient employeeClient;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("123456789");
        user.setEmail("test@example.com");
        user.setAddress("123 Street");
        user.setPassport("AB123456");
        user.setRole(UserRole.EMPLOYEE);
        user.setActive(true);
    }

    @Test
    void testCreateEmployeeRequest_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(employeeClient.upsertEmployee(any(EmployeeRequest.class))).thenReturn(ResponseEntity.ok().build());

        employeeService.createEmployeeRequest(userId);

        verify(employeeClient, times(1)).upsertEmployee(any(EmployeeRequest.class));
    }

    @Test
    void testCreateEmployeeRequest_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.createEmployeeRequest(userId));
    }

    @Test
    void testCreateEmployeeRequest_UserNotEmployee() {
        user.setRole(UserRole.GUEST);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        employeeService.createEmployeeRequest(userId);

        verify(employeeClient, never()).upsertEmployee(any(EmployeeRequest.class));
    }

    @Test
    void testGetEmployeeById_Success() {
        Employee employee = new Employee();
        when(employeeClient.getEmployeeByUserId(userId)).thenReturn(ResponseEntity.ok(employee));

        Employee result = employeeService.getEmployeeById(userId);

        assertNotNull(result);
    }

}
