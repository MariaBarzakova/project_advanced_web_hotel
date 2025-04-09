package app.web;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
    void putRequestToProfile_shouldRedirectToUsers() throws Exception {
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
}
//@PutMapping("/{id}/profile")
//    public ModelAndView updateUserProfile(@PathVariable UUID id, @Valid UserEditRequest userEditRequest,
//                                          BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            User user = userService.getUserById(id);
//            ModelAndView modelAndView = new ModelAndView();
//            modelAndView.setViewName("guest-profile");
//            modelAndView.addObject("user", user);
//            modelAndView.addObject("userEditRequest", userEditRequest);
//            return modelAndView;
//        }
//        userService.editUserDetails(id, userEditRequest);
//        return new ModelAndView("redirect:/rooms");
//    }
