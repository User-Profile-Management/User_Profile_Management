package com.example.task.Controller;

import com.example.task.DTO.CertificateDTO;
import com.example.task.Service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/{userId}/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    public ResponseEntity<List<CertificateDTO>> getCertificates(@PathVariable String userId) {
        List<CertificateDTO> certificates = certificateService.getCertificatesByUserId(userId);
        return certificates.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(certificates);
    }

    @GetMapping("/{certificateId}/download")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Integer certificateId) {
        byte[] pdfData = certificateService.getCertificatePdfById(certificateId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=certificate_" + certificateId + ".pdf")
                .contentType(MediaType.IMAGE_PNG)
                .body(pdfData);
    }


    @PostMapping(consumes = {"multipart/form-data"})  // Support file upload
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<CertificateDTO> addCertificate(
            @PathVariable String userId,
            @RequestParam String certificateName,
            @RequestParam String issuedBy,
            @RequestParam("file") MultipartFile pdfFile) {

        try {
            CertificateDTO createdCertificate = certificateService.addCertificate(userId, certificateName, issuedBy, pdfFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificate);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{certificateId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<String> deleteCertificate(@PathVariable Integer certificateId) {
        try {
            certificateService.deleteCertificateById(certificateId);
            return ResponseEntity.ok("Certificate deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
