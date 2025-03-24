package com.example.task.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_badges")
public class UserBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = true)
    private LocalDateTime deletedAt;
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }


    @ManyToOne
    @JoinColumn(name = "badge_id", referencedColumnName = "id")
    private Badge badge;

}
