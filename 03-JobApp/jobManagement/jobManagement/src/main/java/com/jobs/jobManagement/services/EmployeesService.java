package com.jobs.jobManagement.services;

import com.jobs.jobManagement.entities.Employee;
import com.jobs.jobManagement.exception.EmployeeNotFound;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeesService {

    List<Employee> employeeList = Arrays.asList(
            new Employee(1L,"John", "ABC"),
            new Employee(2L, "John", "XYZ"),
            new Employee(3L,"Roy", "PQR")
    );

    public List<Employee> fetchAllEmployee() {
        return this.employeeList;
    }

    public Employee fetchEmployeebyId(Long employeeId) {
        return this.employeeList
                .stream()
                .filter(x -> x.getId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFound("Employee with ID" + employeeId + " not found"));
    }

    public List<String> fetchCommonNames() {
        return this.employeeList
                .stream()
                .map(x->x.getFirstName())
                .collect(Collectors.toSet())
                .stream()
                .toList();
    }
}
