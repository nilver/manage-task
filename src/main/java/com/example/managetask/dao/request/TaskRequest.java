package com.example.managetask.dao.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotNull(message = "user age is required")
    private String name;
    private String description;
    private Boolean completed ;
    private LocalDate dueDate;
}
