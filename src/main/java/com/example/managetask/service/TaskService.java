package com.example.managetask.service;

import com.example.managetask.dao.request.TaskRequest;
import com.example.managetask.dao.response.TaskResponse;
import com.example.managetask.entity.User;
import com.example.managetask.exception.ResourceNotFoundException;
import com.example.managetask.entity.Task;
import com.example.managetask.repository.TaskRepository;
import com.example.managetask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public TaskResponse createTask(TaskRequest taskRequest, Optional<User> user) {
        //all task has a one month.
        if(taskRequest.getDueDate()==null){
            taskRequest.setDueDate(LocalDate.now().plusMonths(1));
        }
        Task task= new Task()
                .withName(taskRequest.getName())
                .withDescription(taskRequest.getDescription())
                .withCompleted(taskRequest.getCompleted()).withDueDate(taskRequest.getDueDate())
                .withUser(user.get());
        Task savedtask = taskRepository.save(task);
        return buildResponse(savedtask);
    }

    private TaskResponse buildResponse(Task savedtask) {
        return TaskResponse.builder()
                .id(savedtask.getId())
                .name(savedtask.getName())
                .description(savedtask.getDescription())
                .completed(savedtask.getCompleted())
                .dueDate(savedtask.getDueDate())
                .user(savedtask.getUser().getUsername())
                .build();
    }

    public Page<TaskResponse> getAllTasks(Pageable pageable, Optional<Boolean> completed, LocalDate dueDate) {
        Page<Task> allTask;

        Page<TaskResponse> allTaskResponse = Page.empty(pageable);

        if (completed.isPresent()) {
            allTask =  taskRepository.findByCompleted(completed.get(), pageable);
        } else {
            allTask =  taskRepository.findAll(pageable);
        }
        return allTask.map(task -> buildResponse(task));

    }

    public TaskResponse getTaskById(Long id) throws ResourceNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return buildResponse( task);
    }

    public TaskResponse updateTask(Long id, Task taskDetails) throws ResourceNotFoundException
    {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setName(taskDetails.getName());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.getCompleted());
        task.setDueDate(taskDetails.getDueDate());
        return buildResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    public TaskResponse completeTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setCompleted(true);
        return buildResponse(taskRepository.save(task));
    }
}