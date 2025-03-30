package app.web;

import app.employee.client.dto.Employee;
import app.employee.service.EmployeeService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final UserService userService;
    //private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    public EmployeeController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getEmployeePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        //log.debug("EmployeeController: Handling request for /employees");
        User user = userService.getUserById(authenticationMetadata.getUserId());
        Employee employee = employeeService.getEmployeeById(user.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("employee", employee);
        modelAndView.setViewName("employee");
        return modelAndView;
    }
}
