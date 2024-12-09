package com.example.managetask;


import com.example.managetask.dao.response.TaskResponse;
import com.example.managetask.entity.Role;
import com.example.managetask.entity.Task;
import com.example.managetask.entity.User;
import com.example.managetask.exception.ResourceNotFoundException;
import com.example.managetask.repository.TaskRepository;
import com.example.managetask.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task taskOne;
    private Task taskTwo;
    private Task existingTask;
    private Task updatedTaskDetails;

    private Pageable pageable;


    @BeforeEach
    void setUp() {user = new User(1,"Nilver","Viera","nviera","XX", Role.USER,new ArrayList<>());
        taskOne = new Task(1L, "Test Task one", "Description", false, null,new User());
        taskTwo= new Task(2L, "Test Task two", "Description", false, null,new User());
        pageable = PageRequest.of(0, 10);
        existingTask = new Task(1L, "Existing Task", "Existing Description", false, LocalDate.now(), new User());
        updatedTaskDetails = new Task(1L, "Updated Task", "Updated Description", true, LocalDate.now().plusDays(1), new User());

    }

    @Test
    void testGetAllTasksWhenCompletedIsEmpty() {
        //given
        List<Task> tasks = List.of(taskOne, taskTwo);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

        given(taskRepository.findAll(pageable))
                .willReturn(taskPage);
        //when
        LocalDate dueDate = LocalDate.of(2024,12,2);
        Page<TaskResponse> allTasks = taskService.getAllTasks(pageable, Optional.empty(),dueDate);
        //then

        assertThat(allTasks.getContent().size()).isEqualTo(2);
    }

    @Test
    void testGetAllTasksWhenCompletedIsNotEmpty() {
        //given
        LocalDate dueDate = LocalDate.of(2024,12,2);
        List<Task> tasks = List.of(taskOne, taskTwo);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

        given(taskRepository.findByCompleted(true, pageable))
                .willReturn(taskPage);
        //when
        Page<TaskResponse> allTasks = taskService.getAllTasks(pageable, Optional.of (true),dueDate);
        //then

        assertThat(allTasks.getContent().size()).isEqualTo(2);
    }


    @Test
    void testUpdateTask() {
        // given
        given(taskRepository.findById(existingTask.getId())).willReturn(Optional.of(existingTask));
        given(taskRepository.save(existingTask)).willReturn(existingTask);

        // when
        TaskResponse updatedTask = taskService.updateTask(existingTask.getId(), updatedTaskDetails);

        // then
        assertThat(updatedTask.getName()).isEqualTo(updatedTaskDetails.getName());
        assertThat(updatedTask.getDescription()).isEqualTo(updatedTaskDetails.getDescription());
        assertThat(updatedTask.getCompleted()).isEqualTo(updatedTaskDetails.getCompleted());
        assertThat(updatedTask.getDueDate()).isEqualTo(updatedTaskDetails.getDueDate());

        verify(taskRepository).findById(existingTask.getId());
        verify(taskRepository).save(existingTask);
    }

    @Test
    void testUpdateTask_TaskNotFound() {
        // given
        Long taskId = 1L;
        given(taskRepository.findById(taskId)).willReturn(Optional.empty());
        // when & then
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskId, updatedTaskDetails));
    }
}
