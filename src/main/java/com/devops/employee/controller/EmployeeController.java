package com.devops.employee.controller;

import com.devops.employee.model.Employee;
import com.devops.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String listEmployees(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Employee> employees;
        if (search != null && !search.trim().isEmpty()) {
            employees = employeeService.searchEmployees(search);
            model.addAttribute("search", search);
        } else {
            employees = employeeService.getAllEmployees();
        }
        model.addAttribute("employees", employees);
        return "employees/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employees/add";
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee, Model model) {
        try {
            employeeService.saveEmployee(employee);
            return "redirect:/employees?saveSuccess";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "employees/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            model.addAttribute("employee", employee);
            return "employees/edit";
        } catch (IllegalArgumentException e) {
            return "redirect:/employees?error=" + e.getMessage();
        }
    }

    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable("id") Long id, @ModelAttribute("employee") Employee employee, Model model) {
        try {
            employeeService.updateEmployee(id, employee);
            return "redirect:/employees?updateSuccess";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "employees/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        try {
            employeeService.deleteEmployee(id);
            return "redirect:/employees?deleteSuccess";
        } catch (IllegalArgumentException e) {
            return "redirect:/employees?error=" + e.getMessage();
        }
    }

    @GetMapping("/view/{id}")
    public String viewEmployee(@PathVariable("id") Long id, Model model) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            model.addAttribute("employee", employee);
            return "employees/view";
        } catch (IllegalArgumentException e) {
            return "redirect:/employees?error=" + e.getMessage();
        }
    }
}
