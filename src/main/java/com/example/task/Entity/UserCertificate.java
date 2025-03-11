package com.example.task.Entity;

import jakarta.persistence.*;
import lombok.*;



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

    @Column(name = "certificate_id", nullable = false, insertable = false, updatable = false)  // ✅ FIXED
    private int certificateId;

    @ManyToOne
    @JoinColumn(name = "certificate_id", referencedColumnName = "certificate_id")
    private Certificate certificate;
}
