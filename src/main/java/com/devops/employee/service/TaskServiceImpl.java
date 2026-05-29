package com.devops.employee.service;

import com.devops.employee.model.Task;
import com.devops.employee.model.User;
import com.devops.employee.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasksByAssignee(User assignee) {
        return taskRepository.findByAssignee(assignee);
    }

    @Override
    public List<Task> getTasksByAssigneeId(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    @Override
    public Task updateTaskStatus(Long taskId, String status) {
        Task task = getTaskById(taskId);
        String oldStatus = task.getStatus();
        task.setStatus(status);
        Task updated = taskRepository.save(task);

        // If marked as COMPLETED, notify the admin
        if ("COMPLETED".equalsIgnoreCase(status) && !"COMPLETED".equalsIgnoreCase(oldStatus)) {
            String message = String.format("Employee %s %s has completed the task: '%s'",
                    task.getAssignee().getFirstName(), task.getAssignee().getLastName(), task.getTitle());
            notificationService.sendNotificationToAdmins(message);
        }

        return updated;
    }
}
