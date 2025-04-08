package app.employee.client;

import app.employee.client.dto.Employee;
import app.employee.client.dto.EmployeeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "employee-svc", url = "http://localhost:8087/api/v1/employees")
public interface EmployeeClient {

    @PostMapping
    ResponseEntity<Void> upsertEmployee(@RequestBody EmployeeRequest employeeRequest) ;

    @GetMapping("/profile")
    ResponseEntity<Employee> getEmployeeByUserId(@RequestParam(name = "userId") UUID userId) ;

    @GetMapping
    ResponseEntity<List<Employee>> getAllEmployees();

    @PutMapping
    ResponseEntity<Void> updateStatus(@RequestParam(name = "userId") UUID userId) ;

}


