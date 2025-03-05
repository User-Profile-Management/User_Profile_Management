package com.example.task.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certificates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Integer certificateId;

    @Column(name = "certificate_name", nullable = false)
    private String certificateName;

    @Column(name = "issued_by", nullable = false)
    private String issuedBy;

    @Lob
    @Column(name = "certificate_document")
    private byte[] certificateDocument;
}