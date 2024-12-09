package com.example.managetask.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private Long id;
    @NotNull
    @NotNull(message = "Name may not be null")
    private String name;
    private String description;
    private Boolean completed = false;
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}