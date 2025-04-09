package com.example.task.Service;

import com.example.task.DTO.CertificateDTO;
import com.example.task.Entity.Certificate;
import com.example.task.Entity.User;
import com.example.task.Repository.CertificateRepository;
import com.example.task.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        System.out.println("Authenticated User ID: " + userId);


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        boolean exists = certificateRepository.existsByUserUserIdAndCertificateName(userId, certificateName);
        if (exists) {
            throw new RuntimeException("Certificate already exists");
        }


        Certificate certificate = new Certificate();
        certificate.setCertificateName(certificateName);
        certificate.setIssuedBy(issuedBy);
        certificate.setUser(user);
        certificate.setCertificatePdf(pdfFile.getBytes());

        Certificate saved = certificateRepository.save(certificate);

        return new CertificateDTO(saved.getCertificateId(), saved.getCertificateName(), saved.getIssuedBy());
    }


    public void deleteCertificateById(String userId, Integer certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        if (!certificate.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own certificates.");
        }

        certificateRepository.deleteById(certificateId);
    }


}
