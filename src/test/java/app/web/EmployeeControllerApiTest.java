package app.web;

import app.employee.client.dto.Employee;
import app.employee.service.EmployeeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerApiTest {
    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAuthorizedRequestToGetAllEmployees_shouldReturn200OK() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        employeeService.getAllEmployees();
        MockHttpServletRequestBuilder request = get("/employees")
                .with(user(principal));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("employees"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    void putUnauthorizedRequestToSwitchRole_shouldReturn404AndNotFoundView() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "megi", "1234", UserRole.GUEST);
        MockHttpServletRequestBuilder request = put("/employees/{userId}", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void putAuthorizedRequestToSwitchRole_shouldRedirectToUsers() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = put("/employees/{userId}", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));
        verify(employeeService, times(1)).updateStatus(any());
    }

    @Test
    void testGetEmployeePage() throws Exception {
        Employee employee = new Employee();
        UUID userId = UUID.randomUUID();
        when(employeeService.getEmployeeByUserId(userId)).thenReturn(employee);
        MockHttpServletRequestBuilder request = get("/employees/profile")
                .formField("username","gosho");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }


// @GetMapping("/profile")
//    public ModelAndView getEmployeePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
//        Employee employee = employeeService.getEmployeeByUserId(authenticationMetadata.getUserId());
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("employee", employee);
//        modelAndView.setViewName("employee");
//        return modelAndView;
//    }
}