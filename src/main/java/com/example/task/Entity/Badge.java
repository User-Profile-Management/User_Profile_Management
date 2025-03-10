package com.example.task.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "badges") // Changed table name to match its purpose
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    private byte[] image;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL)
    private List<UserBadge> userBadge; // Corrected ManyToOne relationship with UserBadge
}