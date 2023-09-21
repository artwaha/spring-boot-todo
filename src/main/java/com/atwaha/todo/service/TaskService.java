package com.atwaha.todo.service;

import com.atwaha.todo.dao.CollaboratorRepository;
import com.atwaha.todo.dao.TaskRepository;
import com.atwaha.todo.dao.UserRepository;
import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.dto.TaskCount;
import com.atwaha.todo.model.enums.InvitationStatus;
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
            Long invitations = collaboratorRepository.countByUserAndInvitationStatusAndIsEnabled(user, InvitationStatus.PENDING, true);
            Long collaborating = collaboratorRepository.countByUserAndInvitationStatusAndIsEnabled(user, InvitationStatus.ACCEPTED, true);
            Long rejected = collaboratorRepository.countByUserAndInvitationStatusAndIsEnabled(user, InvitationStatus.REJECTED, true);

            TaskCount taskCount = new TaskCount(all, done, pending, invitations, collaborating, rejected);
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

//            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (InterruptedException e) {
//                // do nothing
//            }

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

                if (updatedTask.getIsCompleted() != null) {
                    task.setIsCompleted(updatedTask.getIsCompleted());
                }

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

    public ResponseEntity<List<Task>> getInvitations(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id"));
            List<Task> invitations = collaboratorRepository.findAllByUserAndInvitationStatus(user, InvitationStatus.PENDING)
                    .stream()
                    .map(Collaborator::getTask)
                    .toList();
            return ResponseEntity.ok(invitations);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> getCollaboratingTasks(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id"));
//            List<Task> collaboratingTasks = collaboratorRepository
//                    .findAllByUserAndInvitationStatus(user, InvitationStatus.ACCEPTED)
//                    .stream()
//                    .map(Collaborator::getTask)
//                    .toList();

            List<Task> collaboratingTasks = collaboratorRepository
                    .findAllByUserAndInvitationStatusAndIsEnabled(user, InvitationStatus.ACCEPTED, true)
                    .stream()
                    .map(Collaborator::getTask)
                    .toList();
            return ResponseEntity.ok(collaboratingTasks);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> getRejectedTasks(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id"));
            List<Task> collaboratingTasks = collaboratorRepository
                    .findAllByUserAndInvitationStatus(user, InvitationStatus.REJECTED)
                    .stream()
                    .map(Collaborator::getTask)
                    .toList();
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                // do nothing
//            }
            return ResponseEntity.ok(collaboratingTasks);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Task>> getAllTasks() {
        try {
            List<Task> allTasks = taskRepository.findAll();

            return ResponseEntity.ok(allTasks);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    public ResponseEntity<String> deleteTask(Long taskId, Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid user Id"));
            taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id"));
            boolean isTaskOwner = taskRepository.existsByIdAndCreatedBy(taskId, user);

            if (!isTaskOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                taskRepository.deleteById(taskId);
                return ResponseEntity.ok("Task deleted Successfully");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
