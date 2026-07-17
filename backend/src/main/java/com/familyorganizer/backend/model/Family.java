package com.familyorganizer.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "families")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String secretCode;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<User> members;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Event> events;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Message> messages;
}
