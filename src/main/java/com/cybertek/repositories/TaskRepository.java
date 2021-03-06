package com.cybertek.repositories;

import com.cybertek.entity.Project;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findAllByAssignedEmployee(User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectCode=?1 AND t.taskStatus <> 'COMPLETE'")
    int totalNonCompletedTasks(String projectCode);
    @Query(value = "SELECT COUNT(*) FROM tasks t JOIN projects p ON t.id = p.id WHERE p.project_code = ?1 AND t.task_status = 'COMPLETE'",nativeQuery = true)
    int totalCompletedTasks(String projectCode);

    List<Task> findAllByProject(Project project);

    List<Task> findAllByTaskStatusIsNotAndAssignedEmployee(Status status, User user);

    List<Task> findAllByProjectAssignedManager(User manager);

    List<Task> findAllByTaskStatusIsAndAssignedEmployee(Status status, User user);
}
