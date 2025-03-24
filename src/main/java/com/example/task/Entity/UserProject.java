package com.example.task.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_projects")
public class UserProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "project_id", nullable = false)
    private int projectId;

    @Column(name = "status",nullable = false)

    private String status;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;


    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @Column(nullable = true)
    private LocalDateTime deletedAt;


    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }


    public void setStatus(String status) {
        this.status = status;
    }
}
