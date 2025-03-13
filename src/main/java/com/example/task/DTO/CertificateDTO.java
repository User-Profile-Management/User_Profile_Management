package com.example.task.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {
    private Integer certificateId;
    private String certificateName;
    private String issuedBy;

}
