package com.atwaha.todo.service;

import com.atwaha.todo.dao.CollaboratorRepository;
import com.atwaha.todo.dao.TaskRepository;
import com.atwaha.todo.dao.UserRepository;
import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.enums.InvitationStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Collaborator> createCollaborator(Collaborator collaborator) {
        try {
            Long taskId = collaborator.getTask().getId();
            User user = collaborator.getUser();
            Task task = collaborator.getTask();

//            User cant be collaborator of a task which he created
            Task invalidTask = taskRepository.findByIdAndCreatedBy(taskId, user);
            boolean collaboratorExists = collaboratorRepository.existsByUserAndTask(user, task);

            if (invalidTask != null || collaboratorExists)
                throw new Exception("Invalid Collaborator: User is the owner of the Task");

            Collaborator newCollaborator = collaboratorRepository.save(collaborator);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCollaborator);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Collaborator>> getTaskCollaborators(Long taskId, Long userId) {
        try {
            List<Collaborator> collaborators = collaboratorRepository.findAllByTaskAndUserNotAndInvitationStatus(
                    taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id")),
                    userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id")),
                    InvitationStatus.ACCEPTED);
            return ResponseEntity.ok(collaborators);
        } catch (Exception e) {
            System.err.println("Collaborator Service: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
