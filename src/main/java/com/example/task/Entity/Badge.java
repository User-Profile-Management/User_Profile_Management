package com.example.task.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob

    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @OneToMany(mappedBy = "badge")
    @JsonIgnore
    private List<UserBadge> userBadge;

    public Badge( String name, byte[] image) {

        this.name = name;
        this.image = image;
    }


}