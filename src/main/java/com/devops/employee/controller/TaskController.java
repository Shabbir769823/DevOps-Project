package com.devops.employee.controller;

import com.devops.employee.model.Task;
import com.devops.employee.model.User;
import com.devops.employee.service.TaskService;
import com.devops.employee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public String listTasks(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = userService.findByUsername(auth.getName());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));

        List<Task> tasks;
        if (isAdmin) {
            tasks = taskService.getAllTasks();
        } else {
            tasks = taskService.getTasksByAssignee(currentUser);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("user", currentUser);
        model.addAttribute("isAdmin", isAdmin);
        return "tasks/list";
    }

    @GetMapping("/new")
    public String showAssignForm(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = userService.findByUsername(auth.getName());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
        if (!isAdmin) {
            return "redirect:/tasks?error=AccessDenied";
        }

        // Fetch all users to display in assignee dropdown
        List<User> assignees = userService.getAllUsers().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> "ROLE_USER".equalsIgnoreCase(r.getName())))
                .collect(Collectors.toList());

        model.addAttribute("task", new Task());
        model.addAttribute("assignees", assignees);
        return "tasks/assign";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute("task") Task taskForm, @RequestParam("assigneeId") Long assigneeId, Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        User creator = userService.findByUsername(auth.getName());
        boolean isAdmin = creator.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
        if (!isAdmin) {
            return "redirect:/tasks?error=AccessDenied";
        }

        try {
            User assignee = userService.findById(assigneeId);
            Task task = new Task(taskForm.getTitle(), taskForm.getDescription(), assignee, creator);
            taskService.saveTask(task);
            return "redirect:/tasks?saveSuccess";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<User> assignees = userService.getAllUsers().stream()
                    .filter(u -> u.getRoles().stream().anyMatch(r -> "ROLE_USER".equalsIgnoreCase(r.getName())))
                    .collect(Collectors.toList());
            model.addAttribute("assignees", assignees);
            return "tasks/assign";
        }
    }

    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = userService.findByUsername(auth.getName());
        Task task = taskService.getTaskById(id);

        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
        boolean isAssignee = task.getAssignee().getId().equals(currentUser.getId());

        // Only the assignee or an admin can change status
        if (isAdmin || isAssignee) {
            taskService.updateTaskStatus(id, status);
            return "redirect:/tasks?statusSuccess";
        } else {
            return "redirect:/tasks?error=AccessDenied";
        }
    }
}
