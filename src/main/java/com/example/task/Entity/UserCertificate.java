package com.example.task.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.security.cert.Certificate;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "user_certificates")
public class UserCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "certificate_id", nullable = false)
    private int certificateId;


//    @ManyToOne
//    @JoinColumn(name = "certificate_id", referencedColumnName = "id")
//    @PrimaryKeyJoinColumn
//    private Certificate certificate;
}

