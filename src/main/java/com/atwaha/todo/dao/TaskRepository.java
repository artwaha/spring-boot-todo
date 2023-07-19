package com.atwaha.todo.dao;

import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {


    List<Task> findByCreatedBy(User user);

    Long countByCreatedBy(User user);

    @Query(
            value = "SELECT COUNT(id) FROM tasks t WHERE t.is_completed=true and t.created_by=:userId",
            nativeQuery = true
    )
    Long countDoneTasks(Long userId);

    @Query(
            value = "SELECT COUNT(id) FROM tasks t WHERE t.is_completed=false and t.created_by=:userId",
            nativeQuery = true
    )
    Long countPendingTasks(Long userId);

    @Query(
            value = "SELECT * FROM tasks WHERE tasks.is_completed=true AND tasks.created_by=:userId",
            nativeQuery = true
    )
    List<Task> findDoneTasks(Long userId);

    @Query(
            value = "SELECT * FROM tasks WHERE tasks.is_completed=false AND tasks.created_by=:userId",
            nativeQuery = true
    )
    List<Task> findPendingTasks(Long userId);

    Task findByIdAndCreatedBy(Long taskId, User user);
}
