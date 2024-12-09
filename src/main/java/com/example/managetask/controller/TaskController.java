package com.example.managetask.controller;

import com.example.managetask.dao.request.TaskRequest;
import com.example.managetask.dao.response.TaskResponse;
import com.example.managetask.entity.Role;
import com.example.managetask.entity.User;
import com.example.managetask.exception.ResourceNotFoundException;
import com.example.managetask.service.JwtService;
import com.example.managetask.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import  com.example.managetask.service.TaskService;
import  com.example.managetask.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

import java.time.LocalDate;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService ;

    @Autowired
    private  JwtService jwtService;

    @Autowired
    private UserService userService;


    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) })
    })
    @PostMapping
    public  ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskRequest taskRequest,
                                                    @RequestHeader("Authorization") String auth) {

        final String requestUsername = jwtService.getUsernameFromAuthHeader(auth);
        final Optional<User> userOptional = userService.getUserByName(requestUsername);
        if (userOptional.isEmpty()
                || !userOptional.get().getUsername().equalsIgnoreCase(requestUsername)
                || Role.USER.name().equals(userOptional.get().getRole()) ){
            LOGGER.error("User  {} is not allow", requestUsername);
            throw new RuntimeException(String.format("User '%s' is not allowed to perform this request", requestUsername));
        }
        LOGGER.info("User {} Try to create a new task", requestUsername);
        TaskResponse taskResponse =  taskService.createTask(taskRequest, userOptional);
        return ResponseEntity.ok(taskResponse);
    }


    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all tasks",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) })
    })
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
                                                @RequestParam Optional<Boolean> completed,
                                                @RequestParam(required = false) LocalDate dueDate,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponse> tasks = taskService.getAllTasks(pageable,completed, dueDate);
        LOGGER.info("Use Try to list all tasks");
        return ResponseEntity.ok(tasks);

    }


    @Operation(summary = "Get a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the task",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        try {
            TaskResponse taskResponse = taskService.getTaskById(id);
            return ResponseEntity.ok(taskResponse);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @Operation(summary = "Update a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody Task taskDetails,
                                           @RequestHeader("Authorization") String auth) {
        final String requestUsername = jwtService.getUsernameFromAuthHeader(auth);
        final Optional<User> userOptional = userService.getUserByName(requestUsername);
        if (userOptional.isEmpty()
                || !userOptional.get().getUsername().equalsIgnoreCase(requestUsername)
                || Role.USER.name().equals(userOptional.get().getRole()) ){
            //LOGGER.error("ID  {} task not found", id)
            throw new RuntimeException(String.format("User '%s' is not allowed to perform this request", requestUsername));
        }
        try {
            LOGGER.info("User {} Try to update  task", requestUsername);
            TaskResponse taskResponse = taskService.updateTask(id,taskDetails);
            return ResponseEntity.ok(taskResponse);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @Operation(summary = "Delete a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id,
                                             @RequestHeader("Authorization") String auth) {

        final String requestUsername = jwtService.getUsernameFromAuthHeader(auth);
        final Optional<User> userOptional = userService.getUserByName(requestUsername);
        if (userOptional.isEmpty()
                || !userOptional.get().getUsername().equalsIgnoreCase(requestUsername)
                || Role.USER.name().equals(userOptional.get().getRole()) ){

            throw new RuntimeException(String.format("User '%s' is not allowed to perform this request", requestUsername));
        }
        LOGGER.info("User {} Try to delete a task", requestUsername);

        try {
            LOGGER.info("User {} Try to update  task", requestUsername);
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task was deleted");
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }


    @Operation(summary = "Mark a task as complete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task marked as complete",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id,
                                             @RequestHeader("Authorization") String auth) {
        final String requestUsername = jwtService.getUsernameFromAuthHeader(auth);
        final Optional<User> userOptional = userService.getUserByName(requestUsername);
        if (userOptional.isEmpty()
                || !userOptional.get().getUsername().equalsIgnoreCase(requestUsername)
                || Role.USER.name().equals(userOptional.get().getRole()) ){

            throw new RuntimeException(String.format("User '%s' is not allowed to perform this request", requestUsername));
        }

        try {
            LOGGER.info("User {} Try to complete a task", requestUsername);
            TaskResponse completedTask = taskService.completeTask(id);;
            return ResponseEntity.ok(completedTask);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
