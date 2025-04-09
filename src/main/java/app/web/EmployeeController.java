package app.web;

import app.employee.client.dto.Employee;
import app.employee.service.EmployeeService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final UserService userService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllEmployees(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<Employee> employees = employeeService.getAllEmployees();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("employees", employees);
        modelAndView.setViewName("employees");
        return modelAndView;
    }

    @GetMapping("/profile")
    public ModelAndView getEmployeePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        Employee employee = employeeService.getEmployeeByUserId(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("employee", employee);
        modelAndView.setViewName("employee");
        return modelAndView;
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateStatus(@PathVariable UUID userId){
        employeeService.updateStatus(userId);
        return "redirect:/employees";
    }

}

