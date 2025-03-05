package com.example.task.Mapper;


import com.example.task.DTO.CertificateDTO;
import com.example.task.Entity.Certificates;

public class CertificateMapper {

    public static CertificateDTO toDTO(Certificates certificate) {
        return new CertificateDTO(
                certificate.getCertificateId(),
                certificate.getCertificateName(),
                certificate.getIssuedBy(),
                "/user/certificates/" + certificate.getCertificateId() + "/download"
        );
    }
}
