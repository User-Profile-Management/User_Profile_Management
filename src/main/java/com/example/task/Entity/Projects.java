package com.example.task.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor  // Generates a no-args constructor
@AllArgsConstructor
@Builder
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer id;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "mentor_id", referencedColumnName = "user_id")
    private User mentor;  // Assuming 'User' entity exists

    @Column(name = "status")
    private String status;
}
