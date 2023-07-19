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
public class TaskController {
    private final TaskService taskService;

    @GetMapping("hello-world")
    Task helloWorld() {
        return new Task();
    }

    //    All Tasks or Task Details
    @GetMapping
    ResponseEntity<List<Task>> fetchTasks(@RequestParam(value = "id", required = false) Long taskId) {
        return taskService.fetchTasks(taskId);
    }

    //    New Task
    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    //    Get Tasks for user
    @GetMapping("users")
    ResponseEntity<List<Task>> fetchUserTasks(@RequestParam(value = "id") Long userId) {
        return taskService.fetchUserTasks(userId);
    }

    @GetMapping("{task-id}")
    ResponseEntity<Task> getTaskDetails(@PathVariable(value = "task-id") Long taskId) {
        return taskService.getTaskDetails(taskId);
    }

    @GetMapping("users/{id}/count")
    ResponseEntity<TaskCount> countUserTasks(@PathVariable(value = "id") Long userId) {
        return taskService.countUserTasks(userId);
    }

    @GetMapping("users/{id}/done")
    ResponseEntity<List<Task>> fetchDoneTask(@PathVariable(value = "id") Long userId) {
        return taskService.fetchDoneTask(userId);
    }

    @GetMapping("users/{id}/pending")
    ResponseEntity<List<Task>> fetchPendingTask(@PathVariable(value = "id") Long userId) {
        return taskService.fetchPendingTask(userId);
    }

}
