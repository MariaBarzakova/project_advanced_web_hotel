package app.web;

import app.feedback.model.Feedback;
import app.feedback.service.FeedbackService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.FeedbackRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FeedbackController.class)
public class FeedbackControllerApiTest {
    @MockitoBean
    private FeedbackService feedbackService;
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private UUID userId;
    private AuthenticationMetadata principal;
    private User mockUser;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        principal = new AuthenticationMetadata(userId, "maria", "1234", UserRole.ADMIN);

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("j");
    }

    @Test
    void getRequestToNewFeedbackForm_shouldReturnFormView() throws Exception {
        when(userService.getUserById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/feedbacks/new").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("feedbackRequest"))
                .andExpect(view().name("feedback-form"));
    }

    @Test
    void postRequestToNewFeedback_withValidData_shouldRedirectToHome() throws Exception {
        when(userService.getUserById(userId)).thenReturn(mockUser);

        mockMvc.perform(post("/feedbacks/new")
                        .with(authentication(new TestingAuthenticationToken(principal, null, "ROLE_ADMIN")))
                        .with(csrf())
                        .param("firstName", "J")
                        .param("lastName", "S")
                        .param("message", "ok"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(feedbackService).createFeedback(any(FeedbackRequest.class), eq(mockUser));
    }

    @Test
    void postRequestToNewFeedback_withInvalidData_shouldReturnFormView() throws Exception {
        when(userService.getUserById(userId)).thenReturn(mockUser);
        mockMvc.perform(post("/feedbacks/new")
                        .with(authentication(new TestingAuthenticationToken(principal, null, "ROLE_ADMIN")))
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("message", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("feedback-form"));
    }
}


