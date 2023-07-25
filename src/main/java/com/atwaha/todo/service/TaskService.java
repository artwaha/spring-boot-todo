package com.atwaha.todo.service;

import com.atwaha.todo.dao.TaskRepository;
import com.atwaha.todo.dao.UserRepository;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.dto.TaskCount;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Task> createTask(Task task) {
        try {
            task.setCreatedAt(LocalDateTime.now());
            task.setLastUpdated(LocalDateTime.now());
            return ResponseEntity.ok(taskRepository.save(task));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> fetchUserTasks(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id"));
            List<Task> tasksForUser = taskRepository.findByCreatedBy(user);
            return ResponseEntity.ok(tasksForUser);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<TaskCount> countUserTasks(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User ID"));
            Long all = taskRepository.countByCreatedBy(user);
            Long done = taskRepository.countByCreatedByAndIsCompletedTrue(user);
            Long pending = taskRepository.countByCreatedByAndIsCompletedFalse(user);

            TaskCount taskCount = new TaskCount(all, done, pending);
            return ResponseEntity.ok(taskCount);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Task> getTaskDetails(Long taskId) {
        try {
            Task taskDetails = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id"));
            return ResponseEntity.ok(taskDetails);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> getAllTasks(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid user Id"));
            List<Task> allTasks = taskRepository.findAllByCreatedBy(user);
            return ResponseEntity.ok(allTasks);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> getPendingTasks(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid user Id"));
            List<Task> pendingTasks = taskRepository.findAllByCreatedByAndIsCompletedFalseOrderByPriorityAsc(user);
            return ResponseEntity.ok(pendingTasks);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> getDoneTasks(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid user Id"));
            List<Task> doneTasks = taskRepository.findAllByCreatedByAndIsCompletedTrueOrderByPriorityAsc(user);
            return ResponseEntity.ok(doneTasks);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
