package com.familyorganizer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    // Nombre que se muestra en la app (ej: "Mamá", "Alessandro")
    @Column(nullable = false)
    private String displayName;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // Rol guardado para uso futuro. Por ahora todos son MEMBER.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.MEMBER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    @JsonIgnore
    @ToString.Exclude
    private Family family;
}
