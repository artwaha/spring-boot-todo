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


//    @GetMapping("pending")
//    ResponseEntity<List<Task>> getPendingTasks() {
//        return taskService.getPendingTasks();
//    }

//    @GetMapping("done")
//    ResponseEntity<List<Task>> getDoneTasks() {
//        return taskService.getDoneTasks();
//    }

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

    //    Task Details
    @GetMapping("{task-id}")
    ResponseEntity<Task> getTaskDetails(@PathVariable(value = "task-id") Long taskId) {
        return taskService.getTaskDetails(taskId);
    }

    //    Count Tasks
    @GetMapping("users/{id}/count")
    ResponseEntity<TaskCount> countUserTasks(@PathVariable(value = "id") Long userId) {
        return taskService.countUserTasks(userId);
    }

    //    Get All tasks for user
    @GetMapping("users/{id}")
    ResponseEntity<List<Task>> getAllTasks(@PathVariable(value = "id") Long userId) {
        return taskService.getAllTasks(userId);
    }

    //    Done Tasks for User
    @GetMapping("users/{id}/done")
    ResponseEntity<List<Task>> fetchDoneTask(@PathVariable(value = "id") Long userId) {
//        return taskService.fetchDoneTask(userId);
        return taskService.getDoneTasks(userId);
    }

    //    Pending Tasks for user
    @GetMapping("users/{id}/pending")
    ResponseEntity<List<Task>> fetchPendingTask(@PathVariable(value = "id") Long userId) {
        return taskService.getPendingTasks(userId);
    }
}
