package com.atwaha.todo.dao;

import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCreatedBy(User user);

    Task findByIdAndCreatedBy(Long taskId, User user);

    List<Task> findAllByCreatedBy(User user);

    List<Task> findAllByCreatedByAndIsCompletedFalseOrderByPriorityAsc(User user);

    List<Task> findAllByCreatedByAndIsCompletedTrueOrderByPriorityAsc(User user);

    Long countByCreatedBy(User user);

    Long countByCreatedByAndIsCompletedTrue(User user);

    Long countByCreatedByAndIsCompletedFalse(User user);

}
