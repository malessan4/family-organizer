package com.familyorganizer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Fecha de vencimiento opcional
    @Column(name = "due_date")
    private LocalDate dueDate;

    // Historial de quién hizo qué — guardamos el displayName como string
    // para que quede registrado aunque el usuario cambie su nombre o sea eliminado
    @Column(name = "created_by_name")
    private String createdByName;       // Quién la creó — NUNCA cambia

    @Column(name = "in_progress_by_name")
    private String inProgressByName;    // Quién la está haciendo — solo cuando status = IN_PROGRESS

    @Column(name = "completed_by_name")
    private String completedByName;     // Quién la completó — solo cuando status = DONE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Family family;
}
