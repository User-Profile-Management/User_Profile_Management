package com.example.task.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Integer certificateId;

    @Column(name = "certificate_name", nullable = false)
    private String certificateName;

    @Column(name = "issued_by", nullable = false)
    private String issuedBy;

    @Lob  // Marks the field as Large Object (BLOB)
    @Column(name = "certificate_pdf")
    private byte[] certificatePdf;  // Binary data for the PDF file

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
