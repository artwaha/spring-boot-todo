package com.atwaha.todo.service;

import com.atwaha.todo.dao.CollaboratorRepository;
import com.atwaha.todo.dao.TaskRepository;
import com.atwaha.todo.dao.UserRepository;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.dto.TaskCount;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CollaboratorRepository collaboratorRepository;

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

    public ResponseEntity<Task> getTaskDetails(Long userId, Long taskId) {
        try {

            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id"));
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id"));
//            Check if is owner
            boolean isOwner = taskRepository.existsByIdAndCreatedBy(taskId, user);

//            Check if collaborating
            boolean isCollaborator = collaboratorRepository.existsByUserAndTask(user, task);

            if (isOwner || isCollaborator)
                return ResponseEntity.ok(task);
            else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> getAllTasksForUser(Long userId) {
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

    public ResponseEntity<Task> updateTask(Long userId, Long taskId, Task updatedTask) {
        try {
//            check if user is owner or collaborator
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid user Id"));
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id"));
            boolean isTaskOwner = taskRepository.existsByIdAndCreatedBy(taskId, user);
            boolean isTaskCollaborator = collaboratorRepository.existsByUserAndTask(user, task);

            if (isTaskOwner || isTaskCollaborator) {
                if (updatedTask.getTitle() != null) {
                    task.setTitle(updatedTask.getTitle());
                }
                if (updatedTask.getDescription() != null) {
                    task.setDescription(updatedTask.getDescription());
                }
                if (updatedTask.getPriority() != null) {
                    task.setPriority(updatedTask.getPriority());
                }

                task.setCompleted(updatedTask.isCompleted());

                task.setLastUpdated(LocalDateTime.now());
                task.setUpdatedBy(user);
                Task savedTask = taskRepository.save(task);

                return ResponseEntity.ok(savedTask);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
