package com.example.task.Service;


import com.example.task.DTO.CertificateDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CertificateService {
    List<CertificateDTO> getAllCertificates();
    CertificateDTO getCertificateById(int certificateId);
    CertificateDTO uploadCertificate(String name, String issuedBy, MultipartFile file);
    byte[] getCertificateFile(int certificateId);
    void deleteCertificate(int certificateId);
}
