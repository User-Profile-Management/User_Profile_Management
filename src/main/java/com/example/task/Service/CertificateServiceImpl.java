package com.example.task.Service;



import com.example.task.DTO.CertificateDTO;
import com.example.task.Entity.Certificates;
import com.example.task.Mapper.CertificateMapper;
import com.example.task.Repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;

    @Override
    public List<CertificateDTO> getAllCertificates() {
        return certificateRepository.findAll().stream()
                .map(CertificateMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CertificateDTO getCertificateById(int certificateId) {
        Certificates certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        return CertificateMapper.toDTO(certificate);
    }

    @Override
    public CertificateDTO uploadCertificate(String name, String issuedBy, MultipartFile file) {
        try {
            Certificates certificate = new Certificates();
            certificate.setCertificateName(name);
            certificate.setIssuedBy(issuedBy);
            certificate.setCertificateDocument(file.getBytes());

            Certificates savedCertificate = certificateRepository.save(certificate);
            return CertificateMapper.toDTO(savedCertificate);

        } catch (IOException e) {
            throw new RuntimeException("Error saving certificate file", e);
        }
    }

    @Override
    public byte[] getCertificateFile(int certificateId) {
        Certificates certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        return certificate.getCertificateDocument();
    }

    @Override
    public void deleteCertificate(int certificateId) {
        if (!certificateRepository.existsById(certificateId)) {
            throw new RuntimeException("Certificate not found");
        }
        certificateRepository.deleteById(certificateId);
    }
}