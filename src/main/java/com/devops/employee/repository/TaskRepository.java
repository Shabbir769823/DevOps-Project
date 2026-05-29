package com.devops.employee.repository;

import com.devops.employee.model.Task;
import com.devops.employee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignee(User assignee);
    List<Task> findByAssigneeId(Long assigneeId);
}
