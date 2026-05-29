package com.devops.employee.controller;

import com.devops.employee.model.Task;
import com.devops.employee.model.User;
import com.devops.employee.service.EmployeeService;
import com.devops.employee.service.NotificationService;
import com.devops.employee.service.TaskService;
import com.devops.employee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class DashboardController {

    private final EmployeeService employeeService;
    private final UserService userService;
    private final TaskService taskService;
    private final NotificationService notificationService;

    @Autowired
    public DashboardController(EmployeeService employeeService, UserService userService, 
                               TaskService taskService, NotificationService notificationService) {
        this.employeeService = employeeService;
        this.userService = userService;
        this.taskService = taskService;
        this.notificationService = notificationService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String username = auth.getName();
            try {
                currentUser = userService.findByUsername(username);
                model.addAttribute("user", currentUser);
            } catch (Exception e) {
                // In case DB state and auth are out of sync
            }
        }
        
        model.addAttribute("employeeCount", employeeService.getEmployeeCount());
        
        // Calculate task stats
        long activeTaskCount = 0;
        long pendingTaskCount = 0;
        
        if (currentUser != null) {
            boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
            if (isAdmin) {
                // Admins see all non-completed tasks
                activeTaskCount = taskService.getAllTasks().stream()
                        .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()))
                        .count();
            } else {
                // Employees see their own pending/in-progress tasks
                pendingTaskCount = taskService.getTasksByAssignee(currentUser).stream()
                        .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()))
                        .count();
            }
        }
        
        model.addAttribute("activeTaskCount", activeTaskCount);
        model.addAttribute("pendingTaskCount", pendingTaskCount);
        
        return "dashboard";
    }

    @GetMapping("/health")
    public String health() {
        return "health";
    }
}
