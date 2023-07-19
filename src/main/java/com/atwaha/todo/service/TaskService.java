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
import java.util.logging.Logger;

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

    public ResponseEntity<List<Task>> fetchTasks(Long taskId) {
        try {
            if (taskId == null) {
                return ResponseEntity.ok(taskRepository.findAll());
            } else {
                List<Task> taskById = new ArrayList<>();
                Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id"));
                taskById.add(task);
                return ResponseEntity.ok(taskById);
            }
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
            Long done = taskRepository.countDoneTasks(userId);
            Long pending = taskRepository.countPendingTasks(userId);

            TaskCount taskCount = new TaskCount(all, done, pending);
            return ResponseEntity.ok(taskCount);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> fetchDoneTask(Long userId) {
        try {
            List<Task> doneTasks = taskRepository.findDoneTasks(userId);
            return ResponseEntity.ok(doneTasks);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> fetchPendingTask(Long userId) {
        try {
            List<Task> pendingTasks = taskRepository.findPendingTasks(userId);
            return ResponseEntity.ok(pendingTasks);
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
}
