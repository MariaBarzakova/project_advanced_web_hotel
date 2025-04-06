package app.unitTests;

import app.employee.service.EmployeeService;
import app.exception.AlreadyExistException;
import app.exception.UniqueEmail;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
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

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("A","B","B","a@b");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("A");
        user.setPassword("B");
        user.setEmail("a@b");
        user.setRole(UserRole.GUEST);
        user.setActive(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedOn(LocalDateTime.now());
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.count()).thenReturn(2L);

        userService.registerUser(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository,never()).save(any());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(UniqueEmail.class, () -> userService.registerUser(registerRequest));
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(user.getId());
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(UUID.randomUUID()));
    }

    @Test
    void testSwitchRole_FromGuestToEmployee() {
        user.setRole(UserRole.GUEST);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.switchRole(user.getId());
        assertEquals(UserRole.EMPLOYEE, user.getRole());
        assertTrue(user.getActive());
        verify(userRepository, times(1)).save(user);
        verify(employeeService, times(1)).createEmployeeRequest(user.getId());
    }
    @Test
    void testSwitchRole_FromEmployeeToGuest() {
        user.setRole(UserRole.EMPLOYEE);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.switchRole(user.getId());
        assertFalse(user.getActive());
        assertEquals(UserRole.GUEST, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testUser");
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.loadUserByUsername("nonExistentUser"));
    }
    @Test
    void testLoadUserByUsername_CorrectAuthenticationMetadata() {
        String username = "A";
        User user = User.builder()
                .id(UUID.randomUUID())
                .password("B")
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UserDetails authenticationMetadata = userService.loadUserByUsername(username);
        assertInstanceOf(AuthenticationMetadata.class, authenticationMetadata);
        AuthenticationMetadata result = (AuthenticationMetadata) authenticationMetadata;
        assertEquals(user.getId(), result.getUserId());
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertThat(result.getAuthorities()).hasSize(1);
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testAllUsersInDB_AllExists() {
        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);
        List<User> users = userService.getAllUsers();
        assertThat(users).hasSize(2);
    }

    @Test
    void testAllSetNoActiveUsers_AllExists() {
        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAllByActiveIsTrue()).thenReturn(userList);
        List<User> users = userService.getAllUsersReadyForRenewal();
        assertThat(users).hasSize(2);
    }
    @Test
    void testRegisterUser_FirstUserBecomesAdmin() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.count()).thenReturn(1L);

        userService.registerUser(registerRequest);

        verify(userRepository, times(2)).save(any(User.class));
    }
    @Test
    void testEditUserDetails() {
        UUID userId = UUID.randomUUID();
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .firstName("A")
                .lastName("B")
                .phoneNumber("012345")
                .address("S")
                .passport("AB123")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.editUserDetails(userId, userEditRequest);

        assertEquals("A", user.getFirstName());
        assertEquals("B", user.getLastName());
        assertEquals("012345", user.getPhoneNumber());
        assertEquals("S", user.getAddress());
        assertEquals("AB123", user.getPassport());
        assertTrue(user.getActive());
        verify(userRepository, times(1)).save(user);
        verify(employeeService, times(1)).createEmployeeRequest(user.getId());
    }
}
