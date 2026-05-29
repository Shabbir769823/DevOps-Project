package com.devops.employee.controller;

import com.devops.employee.model.User;
import com.devops.employee.service.EmployeeService;
import com.devops.employee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final EmployeeService employeeService;
    private final UserService userService;

    @Autowired
    public DashboardController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String username = auth.getName();
            try {
                User currentUser = userService.findByUsername(username);
                model.addAttribute("user", currentUser);
            } catch (Exception e) {
                // In case DB state and auth are out of sync (e.g. test environments)
            }
        }
        
        model.addAttribute("employeeCount", employeeService.getEmployeeCount());
        return "dashboard";
    }
}
