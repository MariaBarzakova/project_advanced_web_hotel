package app;

import app.employee.service.EmployeeService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .username("testUser")
                .password("encodedPassword")
                .email("test@example.com")
                .role(UserRole.GUEST)
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
    }


    @Test
    void shouldRegisterUserSuccessfully() {

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.count()).thenReturn(1L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.registerUser(registerRequest);

        verify(userRepository, times(2)).save(any(User.class));

    }

    @Test
    void whenChangeGuestRole_theEmployeeRoleIsAssigned() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.GUEST)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.switchRole(userId);
        assertEquals(UserRole.EMPLOYEE, user.getRole());
    }

    @Test
    void whenChangeEmployeeRole_theGuestRoleIsAssigned() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.EMPLOYEE)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.switchRole(userId);
        assertEquals(UserRole.GUEST, user.getRole());
    }


}
