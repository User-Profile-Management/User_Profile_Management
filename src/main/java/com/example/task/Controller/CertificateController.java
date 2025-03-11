package com.example.task.Controller;


import com.example.task.DTO.CertificateDTO;
import com.example.task.Service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/user/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    public ResponseEntity<List<CertificateDTO>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<CertificateDTO> getCertificateById(@PathVariable int certificateId) {
        return ResponseEntity.ok(certificateService.getCertificateById(certificateId));
    }

    @PostMapping
    public ResponseEntity<CertificateDTO> uploadCertificate(
            @RequestParam String name,
            @RequestParam String issuedBy,
            @RequestParam MultipartFile file) {

        return ResponseEntity.status(201).body(certificateService.uploadCertificate(name, issuedBy, file));
    }

    @GetMapping("/{certificateId}/download")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable int certificateId) {
        byte[] fileData = certificateService.getCertificateFile(certificateId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate_" + certificateId + ".pdf")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @DeleteMapping("/{certificateId}")
    public ResponseEntity<String> deleteCertificate(@PathVariable int certificateId) {
        certificateService.deleteCertificate(certificateId);
        return ResponseEntity.ok("Certificate deleted successfully");
    }
}
