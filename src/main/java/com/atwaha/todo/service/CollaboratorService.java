package com.atwaha.todo.service;

import com.atwaha.todo.dao.CollaboratorRepository;
import com.atwaha.todo.dao.TaskRepository;
import com.atwaha.todo.dao.UserRepository;
import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.dto.CollaboratorRequest;
import com.atwaha.todo.model.enums.InvitationStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public ResponseEntity<List<Collaborator>> getTaskCollaborators(Long taskId, Long userId) {
        try {
//            List<Collaborator> collaborators = collaboratorRepository.findAllByTaskAndUserNotAndInvitationStatus(
//                    taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id")),
//                    userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id")),
//                    InvitationStatus.ACCEPTED);

            List<Collaborator> collaborators = collaboratorRepository.findAllByTaskAndUserNotAndInvitationStatusAndIsEnabled(
                    taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id")),
                    userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id")),
                    InvitationStatus.ACCEPTED, true);
            return ResponseEntity.ok(collaborators);
        } catch (Exception e) {
            System.err.println("Collaborator Service: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Collaborator>> removeCollaborator(List<CollaboratorRequest> collaborators) {
        try {

            List<Collaborator> response = new ArrayList<>();

            collaborators.forEach(collaborator -> {
                User user = userRepository.findById(collaborator.getUserId()).orElseThrow(() -> new EntityNotFoundException("User Id Invalid"));
                Task task = taskRepository.findById(collaborator.getTaskId()).orElseThrow(() -> new EntityNotFoundException("Task Id Invalid"));
                Collaborator existingCollaborator = collaboratorRepository.findByUserAndTask(user, task);
                if (existingCollaborator != null) {
//                    existingCollaborator.setInvitationStatus(InvitationStatus.REJECTED);
                    existingCollaborator.setIsEnabled(false);
                    Collaborator updatedCollaborator = collaboratorRepository.save(existingCollaborator);
                    response.add(updatedCollaborator);
                }
            });

            return
                    response.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Collaborator Service: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Collaborator> createCollaborator(CollaboratorRequest collaborator) {
        try {
            Long taskId = collaborator.getTaskId();
            User user = userRepository.findById(collaborator.getUserId()).orElseThrow(() -> new EntityNotFoundException("User Id Invalid"));
            Task task = taskRepository.findById(collaborator.getTaskId()).orElseThrow(() -> new EntityNotFoundException("Task Id Invalid"));

//            User cant be collaborator of a task which he created
            boolean invalidTask = taskRepository.existsByIdAndCreatedBy(taskId, user);
            boolean collaboratorExists = collaboratorRepository.existsByUserAndTaskAndInvitationStatusAndIsEnabled(user, task, InvitationStatus.ACCEPTED, true);

            if (invalidTask || collaboratorExists) {
                throw new Exception("Invalid Collaborator: User is the owner of the Task- or Already collaborating");
            } else {
                if (collaboratorRepository.existsByUserAndTaskAndInvitationStatusNot(user, task, InvitationStatus.ACCEPTED)) {
                    Collaborator coll = collaboratorRepository.findByUserAndTask(user, task);
                    coll.setInvitationStatus(InvitationStatus.ACCEPTED);
                    return ResponseEntity.ok(collaboratorRepository.save(coll));
                } else {
                    Collaborator newCollaborator = new Collaborator();
                    newCollaborator.setUser(user);
                    newCollaborator.setTask(task);
                    newCollaborator.setInvitationStatus(InvitationStatus.ACCEPTED);
                    return ResponseEntity.status(HttpStatus.CREATED).body(collaboratorRepository.save(newCollaborator));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Collaborator>> inviteUsers(List<CollaboratorRequest> usersToInvite) {
        try {
            List<Collaborator> response = new ArrayList<>();

            usersToInvite.forEach(userToInvite -> {
                User user = userRepository.findById(userToInvite.getUserId()).orElseThrow(() -> new EntityNotFoundException("User Id Invalid"));
                Task task = taskRepository.findById(userToInvite.getTaskId()).orElseThrow(() -> new EntityNotFoundException("Task Id Invalid"));
                //            User cant be collaborator of a task which he created
                boolean invalidTask = taskRepository.existsByIdAndCreatedBy(userToInvite.getTaskId(), user);
                boolean collaboratorExists = collaboratorRepository.existsByUserAndTask(user, task);

                if (!invalidTask && !collaboratorExists) {
                    Collaborator newCollaborator = new Collaborator();
                    newCollaborator.setUser(user);
                    newCollaborator.setTask(task);
                    Collaborator savedCollaborator = collaboratorRepository.save(newCollaborator);
                    response.add(savedCollaborator);
                }
            });

            return
                    response.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Collaborator Service: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
