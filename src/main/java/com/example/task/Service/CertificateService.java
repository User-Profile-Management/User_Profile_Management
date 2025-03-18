package com.example.task.Service;

import com.example.task.DTO.CertificateDTO;
import com.example.task.Entity.Certificate;
import com.example.task.Entity.User;
import com.example.task.Repository.CertificateRepository;
import com.example.task.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    public List<CertificateDTO> getCertificatesByUserId(String userId) {
        List<Certificate> certificates = certificateRepository.findByUserUserId(userId);
        return certificates.stream()
                .map(c -> new CertificateDTO(c.getCertificateId(), c.getCertificateName(), c.getIssuedBy()))
                .collect(Collectors.toList());
    }

    public byte[] getCertificatePdfById(Integer certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        return certificate.getCertificatePdf();
    }


    public CertificateDTO addCertificate(String userId, String certificateName, String issuedBy, MultipartFile pdfFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the certificate already exists for the user
        boolean exists = certificateRepository.existsByUserAndCertificateName(user, certificateName);
        if (exists) {
            throw new RuntimeException("Certificate already exists");
        }

        Certificate certificate = new Certificate();
        certificate.setCertificateName(certificateName);
        certificate.setIssuedBy(issuedBy);
        certificate.setUser(user);
        certificate.setCertificatePdf(pdfFile.getBytes());  // Convert PDF to byte[]

        Certificate saved = certificateRepository.save(certificate);
        return new CertificateDTO(saved.getCertificateId(), saved.getCertificateName(), saved.getIssuedBy());
    }

    // DELETE Certificate by ID
    public void deleteCertificateById(Integer certificateId) {
        if (!certificateRepository.existsById(certificateId)) {
            throw new RuntimeException("Certificate not found with ID: " + certificateId);
        }
        certificateRepository.deleteById(certificateId);
    }
}
