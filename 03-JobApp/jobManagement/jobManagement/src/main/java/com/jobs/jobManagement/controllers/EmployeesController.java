package com.jobs.jobManagement.controllers;

import com.jobs.jobManagement.entities.Employee;
import com.jobs.jobManagement.services.EmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeesController {

    @Autowired
    EmployeesService employeesService;

    @GetMapping("/")
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employeeList = employeesService.fetchAllEmployee();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeList);
    }

    @GetMapping("/:id")
    public ResponseEntity<?> getEmployeeById(@RequestParam Long employeeId) {
        Employee employee = employeesService.fetchEmployeebyId(employeeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employee);
    }
    
    @GetMapping("/common")
    public ResponseEntity<?> getCommonEmployeeName() {
        List<String> commonEmployeeNames = employeesService.fetchCommonNames();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(commonEmployeeNames);
    }
}
