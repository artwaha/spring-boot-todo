package com.atwaha.todo.controller;

import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.dto.CollaboratorRequest;
import com.atwaha.todo.service.CollaboratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/collaborators")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CollaboratorController {
    private final CollaboratorService collaboratorService;

    //    Create Collaborator / invite user
    @PostMapping
    ResponseEntity<Collaborator> createCollaborator(@RequestBody CollaboratorRequest collaborator) {
        return collaboratorService.createCollaborator(collaborator);
    }

    @PatchMapping("invite")
    ResponseEntity<List<Collaborator>> inviteCollaborators(@RequestBody List<CollaboratorRequest> usersToInvite) {
//        return collaboratorService.createCollaborator(collaborator);
        return collaboratorService.inviteUsers(usersToInvite);
    }

    //    Get Collaborators for task
    @GetMapping("tasks/{task-id}/users/{user-id}")
    ResponseEntity<List<Collaborator>> getTaskCollaborators(@PathVariable(value = "task-id") Long taskId, @PathVariable(value = "user-id") Long userId) {
        return collaboratorService.getTaskCollaborators(taskId, userId);
    }

    //    Remove Collaborator
//    TODO handle multiple
    @PatchMapping("remove")
    ResponseEntity<List<Collaborator>> removeCollaborator(@RequestBody List<CollaboratorRequest> collaborators) {
        return collaboratorService.removeCollaborator(collaborators);
    }

    //    Remove pending invitation
    @PatchMapping("pending-invitation/remove")
    ResponseEntity<List<Collaborator>> removePendingInvitation(@RequestBody List<CollaboratorRequest> collaboratorS) {
        return collaboratorService.removeCollaborator(collaboratorS);
    }
}
