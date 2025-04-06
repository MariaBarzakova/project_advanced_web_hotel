package app.web;

import app.employee.client.dto.Employee;
import app.employee.service.EmployeeService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void getRequestToEmployeesPoint_shouldReturnEmployeeView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "john", "12345", UserRole.ADMIN);

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("john");

        Employee mockEmployee = new Employee();
        mockEmployee.setUserId(userId);

        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(employeeService.getEmployeeById(userId)).thenReturn(mockEmployee);
        MockHttpServletRequestBuilder request = get("/employees").with(user(principal));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("employee"))
                .andExpect(view().name("employee"));
    }
}




