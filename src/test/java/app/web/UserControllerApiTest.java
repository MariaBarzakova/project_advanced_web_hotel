package app.web;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.UserEditRequest;
import app.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

import static app.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {
    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void putUnauthorizedRequestToSwitchRole_shouldReturn404AndNotFoundView() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "megi", "1234", UserRole.GUEST);

        MockHttpServletRequestBuilder request = put("/users/{id}/role",UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("not-found"));
    }

    @Test
    void putAuthorizedRequestToSwitchRole_shouldRedirectToUsers() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        verify(userService, times(1)).switchRole(any());
    }

    @Test
    void getAuthorizedRequestToGetAllUsers_shouldReturn200OK() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        userService.getAllUsers();
        MockHttpServletRequestBuilder request = get("/users")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"));
    }
    @Test
    void getRequestToGetUser_shouldRedirect() throws Exception {
        userService.getAllUsers();
        MockHttpServletRequestBuilder request = get("/users/{id}/profile",UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void putRequestToProfile_shouldRedirectToRooms() throws Exception {
        //when(userService.editUserDetails(UUID.randomUUID(),any())).thenReturn();
        MockHttpServletRequestBuilder request = put("/users/{id}/profile", UUID.randomUUID())
                .formField("firstName","")
                .formField("lastName","")
                .formField("phoneNumber","")
                .formField("address","")
                .formField("passport","")
                .formField("active","")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getUserProfile_shouldReturnGuestProfileViewWithUserData() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = aRandomUser();
        when(userService.getUserById(userId)).thenReturn(user);

        AuthenticationMetadata principal = new AuthenticationMetadata(
                UUID.randomUUID(), "megi", "1234", UserRole.GUEST);

        mockMvc.perform(get("/users/{id}/profile", userId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("guest-profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userEditRequest"));
    }
    

    @Test
    void putInvalidUserProfileUpdate_shouldReturnGuestProfileView() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = aRandomUser();
        AuthenticationMetadata principal = new AuthenticationMetadata(
                UUID.randomUUID(), "megi", "password", UserRole.GUEST
        );
        when(userService.getUserById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder request = put("/users/{id}/profile", userId)
                .with(user(principal))
                .param("firstName", "") // empty = invalid
                .param("lastName", "D")
                .param("phoneNumber", "")
                .param("address", "")
                .param("passport", "")
                .param("active", "true")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("guest-profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userEditRequest"));
    }
}
