package com.example.task.Service;

import com.example.task.Entity.UserCertificate;
import com.example.task.Entity.Certificate;
import com.example.task.Repository.UserCertificateRepository;
import com.example.task.Repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCertificateService {
    private final UserCertificateRepository userCertificateRepository;
    private final CertificateRepository certificateRepository;

    @Autowired
    public UserCertificateService(UserCertificateRepository userCertificateRepository, CertificateRepository certificateRepository) {
        this.userCertificateRepository = userCertificateRepository;
        this.certificateRepository = certificateRepository;
    }

    public List<UserCertificate> getAllUserCertificates() {
        return userCertificateRepository.findAll();
    }


    public List<UserCertificate> getUserCertificatesByUserId(String userId) {
        return userCertificateRepository.findByUserId(userId);
    }


    public UserCertificate addUserCertificate(String userId, int certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        UserCertificate userCertificate = new UserCertificate();
        userCertificate.setUserId(userId);
        userCertificate.setCertificate(certificate);

        return userCertificateRepository.save(userCertificate);
    }


    public void deleteUserCertificate(int id) {
        userCertificateRepository.deleteById(id);
    }
}
