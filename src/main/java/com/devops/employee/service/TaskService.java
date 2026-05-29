package com.devops.employee.service;

import com.devops.employee.model.Task;
import com.devops.employee.model.User;
import java.util.List;

public interface TaskService {
    Task saveTask(Task task);
    Task getTaskById(Long id);
    List<Task> getAllTasks();
    List<Task> getTasksByAssignee(User assignee);
    List<Task> getTasksByAssigneeId(Long assigneeId);
    Task updateTaskStatus(Long taskId, String status);
}
