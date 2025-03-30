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

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEmployee(@PathVariable("id") UUID id) ;
}


