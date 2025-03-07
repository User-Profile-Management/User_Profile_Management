package com.example.task.Mapper;

import com.example.task.DTO.UserCertificateDTO;
import com.example.task.Entity.UserCertificate;
import org.springframework.stereotype.Component;


public class UserCertificateMapper {
    public static UserCertificateDTO mapToUserCertificateDTO(UserCertificate userCertificate) {
        return new UserCertificateDTO(
                userCertificate.getId(),
                userCertificate.getUserId(),
                userCertificate.getCertificate().getId()
        );
    }


    public static UserCertificate mapToUserCertificate(UserCertificateDTO userCertificateDTO) {
        return new UserCertificate(
                userCertificateDTO.getId(),
                userCertificateDTO.getUserId(),
                userCertificateDTO.getCertificateId()
        );
    }
}
