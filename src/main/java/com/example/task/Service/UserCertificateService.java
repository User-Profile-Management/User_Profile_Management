package com.example.task.Service;

import com.example.task.Entity.UserCertificate;
import com.example.task.Entity.Certificate;
import com.example.task.Repository.UserCertificateRepository;
import com.example.task.Repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Get all user certificates
    public List<UserCertificate> getAllUserCertificates() {
        return userCertificateRepository.findAll();
    }

    // Get certificates for a specific user
    public List<UserCertificate> getUserCertificatesByUserId(String userId) {
        return userCertificateRepository.findByUserId(userId);
    }

    // Add a certificate to a user
    public UserCertificate addUserCertificate(String userId, int certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found with ID: " + certificateId));

        UserCertificate userCertificate = new UserCertificate();
        userCertificate.setUserId(userId);
        userCertificate.setCertificate(certificate);

        return userCertificateRepository.save(userCertificate);
    }

    // Delete a user certificate safely
    @Transactional
    public void deleteUserCertificate(int id) {
        if (!userCertificateRepository.existsById(id)) {
            throw new RuntimeException("UserCertificate not found with ID: " + id);
        }
        userCertificateRepository.deleteById(id);
    }
}
