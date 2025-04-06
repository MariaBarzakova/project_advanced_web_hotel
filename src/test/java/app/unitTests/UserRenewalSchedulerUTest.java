package app.unitTests;

import app.scheduler.UserRenewalScheduler;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRenewalSchedulerUTest {
    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRenewalScheduler userRenewalScheduler;

    private User regularUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setUpdatedOn(LocalDateTime.now().minusDays(2)); // User last updated more than 1 day ago
        regularUser.setActive(true);
        regularUser.setRole(UserRole.EMPLOYEE);

        adminUser = new User();
        adminUser.setUpdatedOn(LocalDateTime.now().minusDays(2));
        adminUser.setActive(true);
        adminUser.setRole(UserRole.ADMIN);
    }

    @Test
    void testRenewUserStatus_WithUsersForRenewal() {
        when(userService.getAllUsersReadyForRenewal()).thenReturn(Arrays.asList(regularUser));
        long days = 1L;
        userRenewalScheduler.renewUserStatus();
        assertFalse(regularUser.getActive());
        verify(userRepository, times(1)).save(regularUser);
    }

    @Test
    void testRenewUserStatus_NoUsersForRenewal() {
        when(userService.getAllUsersReadyForRenewal()).thenReturn(Collections.emptyList());

        userRenewalScheduler.renewUserStatus();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRenewUserStatus_AdminUserNotDeactivated() {
        when(userService.getAllUsersReadyForRenewal()).thenReturn(Arrays.asList(adminUser));

        userRenewalScheduler.renewUserStatus();

        assertTrue(adminUser.getActive());
        verify(userRepository, never()).save(adminUser);
    }
}
