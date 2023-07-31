package com.atwaha.todo.controller;

import com.atwaha.todo.model.User;
import com.atwaha.todo.model.dto.LoginRequestDTO;
import com.atwaha.todo.model.dto.UserResponseDTO;
import com.atwaha.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    // All users
    @GetMapping()
    ResponseEntity<List<User>> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    ResponseEntity<UserResponseDTO> register(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("login")
    ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        return userService.login(loginRequest);
    }

    //    Users to invite
    @GetMapping("{user-id}/tasks/{task-id}")
    ResponseEntity<List<User>> getUsersToInvite(@PathVariable(name = "user-id") Long userId, @PathVariable(name = "task-id") Long taskId) {
        return userService.getUsersToInvite(userId, taskId);
    }

    //    Pending Invitations
    @GetMapping("{user-id}/tasks/{task-id}/pending")
    ResponseEntity<List<User>> getPendingInvitations(@PathVariable(name = "user-id") Long userId, @PathVariable(name = "task-id") Long taskId) {
        return userService.getPendingInvitations(userId, taskId);
    }
}
