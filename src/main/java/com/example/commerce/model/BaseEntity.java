package com.example.commerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
