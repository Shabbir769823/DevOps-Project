package com.devops.employee.service;

import com.devops.employee.model.Employee;
import com.devops.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employee1 = new Employee("John", "Doe", "john.doe@company.com", "Engineering", "Developer", 80000.0);
        employee1.setId(1L);

        employee2 = new Employee("Jane", "Smith", "jane.smith@company.com", "HR", "Manager", 75000.0);
        employee2.setId(2L);
    }

    @Test
    void getAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        List<Employee> list = employeeService.getAllEmployees();
        assertEquals(2, list.size());
        assertEquals("John", list.get(0).getFirstName());
    }

    @Test
    void getEmployeeById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        Employee emp = employeeService.getEmployeeById(1L);
        assertNotNull(emp);
        assertEquals("Doe", emp.getLastName());
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById(99L));
    }

    @Test
    void saveEmployee_Success() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        Employee newEmp = new Employee("John", "Doe", "john.doe@company.com", "Engineering", "Developer", 80000.0);
        Employee saved = employeeService.saveEmployee(newEmp);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }

    @Test
    void saveEmployee_DuplicateEmail() {
        when(employeeRepository.existsByEmail("john.doe@company.com")).thenReturn(true);

        Employee newEmp = new Employee("John", "Doe", "john.doe@company.com", "Engineering", "Developer", 80000.0);
        assertThrows(IllegalArgumentException.class, () -> employeeService.saveEmployee(newEmp));
    }
}
