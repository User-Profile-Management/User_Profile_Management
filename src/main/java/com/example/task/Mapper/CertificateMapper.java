package com.example.task.Mapper;


import com.example.task.DTO.CertificateDTO;
import com.example.task.Entity.Certificate;

public class CertificateMapper {

    public static CertificateDTO toDTO(Certificate certificate) {
        return new CertificateDTO(
                certificate.getCertificateId(),
                certificate.getCertificateName(),
                certificate.getIssuedBy(),
                "/user/certificates/" + certificate.getCertificateId() + "/download"
        );
    }
}
