package com.example.managetask.dao.response;

import com.example.managetask.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean completed ;
    private LocalDate dueDate;
    private String user;
}
