package app.web;

import app.exception.AlreadyExistException;
import app.security.AuthenticationMetadata;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static app.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {
        MockHttpServletRequestBuilder request = get("/");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {
        MockHttpServletRequestBuilder request = get("/register");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginView() throws Exception {
        MockHttpServletRequestBuilder request = get("/login");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"));
    }

    @Test
    void getRequestToLoginEndpointWithLoginParameter_shouldReturnLoginViewAndErrorMessageAttribute() throws Exception {
        MockHttpServletRequestBuilder request = get("/login").param("error", "");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest", "errorMessage"));
    }

    @Test
    void postRequestToRegisterEndpoint_shouldRedirectToLoginPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "megi")
                .formField("password", "1234")
                .formField("confirmPassword", "1234")
                .formField("email", "megi@megi")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(userService, times(1)).registerUser(any());

    }
    @Test
    void postRequestToRegisterEndpointWhenUsernameAlreadyExist_thenRedirectToRegisterWithFlashParameter() throws Exception {
        when(userService.registerUser(any())).thenThrow(new AlreadyExistException("User with username already exists!"));
        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "megi")
                .formField("password", "1234")
                .formField("confirmPassword", "1234")
                .formField("email", "megi@megi")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("alreadyExistMessage"));
        verify(userService, times(1)).registerUser(any());

    }
    @Test
    void postRequestToRegisterEndpointWithInvalidData_returnRegisterView() throws Exception{
        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "")
                .formField("password", "")
                .formField("confirmPassword", "1234")
                .formField("email", "megi@megi")
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
        verify(userService,never()).registerUser(any());
    }
    @Test
    void getAuthenticatedRequestToHome_returnsHomeView() throws Exception {
        when(userService.getUserById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "maria", "1234", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = get("/home")
                .with(user(principal));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"));
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUnauthenticatedRequestToHome_redirectToLogin() throws Exception {
        MockHttpServletRequestBuilder request = get("/home");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
        verify(userService, never()).getUserById(any());
    }

}
