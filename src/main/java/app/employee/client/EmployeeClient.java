package app.employee.client;

import app.employee.client.dto.Employee;
import app.employee.client.dto.EmployeeRequest;
import app.web.mapper.DtoMapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "employee-svc", url = "http://localhost:8087/api/v1/employees")
public interface EmployeeClient {

    @PostMapping
    ResponseEntity<Void> upsertEmployee(@RequestBody EmployeeRequest employeeRequest) ;

    @GetMapping
    ResponseEntity<Employee> getEmployeeByUserId(@RequestParam(name = "userId") UUID userId) ;

//    @PutMapping
//    ResponseEntity<Void>updateEmployeeActive(@RequestBody EmployeeRequest employeeRequest);
//    @PutMapping
//    ResponseEntity<Void>updateEmployeeActive(@RequestParam(name = "userId") UUID userId,@RequestParam("enabled") boolean enabled);

    @DeleteMapping
    ResponseEntity<Void> deleteEmployee(@RequestParam(name = "userId") UUID userId) ;
}
