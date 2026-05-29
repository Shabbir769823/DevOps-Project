package com.devops.employee.service;

import com.devops.employee.model.Employee;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee saveEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employeeDetails);
    void deleteEmployee(Long id);
    List<Employee> searchEmployees(String keyword);
    long getEmployeeCount();
}
