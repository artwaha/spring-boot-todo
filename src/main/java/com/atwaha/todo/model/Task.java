package com.atwaha.todo.model;

import com.atwaha.todo.model.enums.Priority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.LOW;

    private boolean isCompleted;
    private LocalDateTime createdAt;

    //    @OneToMany
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
