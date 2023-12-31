package com.atwaha.todo.controller;

import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.dto.TaskCount;
import com.atwaha.todo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
//TODO: CORS
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {
    private final TaskService taskService;

    //    New Task
    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("{task-id}/users/{user-id}")
    ResponseEntity<Task> updateTask(@RequestBody Task task, @PathVariable(value = "task-id") Long taskId, @PathVariable(value = "user-id") Long userId) {
        return taskService.updateTask(userId, taskId, task);
    }

    //    Task Details( User can only view Task Details of Tasks he created only and Collaborating
    @GetMapping("{task-id}/users/{user-id}")
    ResponseEntity<Task> getTaskDetails(@PathVariable(value = "user-id") Long userId, @PathVariable(value = "task-id") Long taskId) {
        return taskService.getTaskDetails(userId, taskId);
    }

    //    Count Tasks
    @GetMapping("users/{id}/count")
    ResponseEntity<TaskCount> countUserTasks(@PathVariable(value = "id") Long userId) {
        return taskService.countUserTasks(userId);
    }

    //    Get All tasks for user
    @GetMapping("users/{id}")
    ResponseEntity<List<Task>> getAllTasksForUser(@PathVariable(value = "id") Long userId) {
        return taskService.getAllTasksForUser(userId);
    }

    //    Done Tasks for User
    @GetMapping("users/{id}/done")
    ResponseEntity<List<Task>> getDoneTask(@PathVariable(value = "id") Long userId) {
        return taskService.getDoneTasks(userId);
    }

    //    Pending Tasks for user
    @GetMapping("users/{id}/pending")
    ResponseEntity<List<Task>> fetchPendingTask(@PathVariable(value = "id") Long userId) {
        return taskService.getPendingTasks(userId);
    }

    //    Invitations
    @GetMapping("users/{id}/invitations")
    ResponseEntity<List<Task>> getInvitations(@PathVariable(value = "id") Long userId) {
        return taskService.getInvitations(userId);
    }

    //    Tasks to which user is collaborating
    @GetMapping("users/{id}/collaborating")
    ResponseEntity<List<Task>> getCollaboratingTasks(@PathVariable(value = "id") Long userId) {
        return taskService.getCollaboratingTasks(userId);
    }

    @GetMapping("users/{id}/rejected")
    ResponseEntity<List<Task>> getRejectedTasks(@PathVariable(value = "id") Long userId) {
        return taskService.getRejectedTasks(userId);
    }
}
