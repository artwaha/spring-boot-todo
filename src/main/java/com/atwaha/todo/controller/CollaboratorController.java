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

//    @PostMapping
//    ResponseEntity<Collaborator> createCollaborator(@RequestBody Collaborator collaborator) {
//        return collaboratorService.createCollaborator(collaborator);
//    }

    @PostMapping
    ResponseEntity<Collaborator> createCollaborator(@RequestBody CollaboratorRequest collaborator) {
        return collaboratorService.createCollaborator(collaborator);
    }

    //    Get Collaborators for task
    @GetMapping("tasks/{task-id}/users/{user-id}")
    ResponseEntity<List<Collaborator>> getTaskCollaborators(@PathVariable(value = "task-id") Long taskId, @PathVariable(value = "user-id") Long userId) {
        return collaboratorService.getTaskCollaborators(taskId, userId);
    }
}
