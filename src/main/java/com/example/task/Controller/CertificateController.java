package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.CertificateDTO;
import com.example.task.Repository.UserRepository;
import com.example.task.Service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final UserRepository userRepository;



    // Get all certificates for the authenticated user
    @GetMapping
    public ResponseEntity<List<CertificateDTO>> getCertificates() {
        String userId = getAuthenticatedUserId();
        List<CertificateDTO> certificates = certificateService.getCertificatesByUserId(userId);
        return certificates.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(certificates);
    }

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        String email = authentication.getName();
        System.out.println("Extracted email from token: " + email);

        return userRepository.findByEmail(email)
                .map(user -> {
                    System.out.println("User found: " + user.getUserId()); // Debug log
                    return user.getUserId();
                })
                .orElseThrow(() -> {
                    System.out.println("User not found for email: " + email);
                    return new RuntimeException("User not found for email: " + email);
                });
    }





    // Download a certificate PDF
    @GetMapping("/{certificateId}/download")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Integer certificateId) {
        byte[] pdfData = certificateService.getCertificatePdfById(certificateId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=certificate_" + certificateId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    //Add a certificate (Only for authenticated user)
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<CertificateDTO> addCertificate(
            @RequestParam String certificateName,
            @RequestParam String issuedBy,
            @RequestParam("file") MultipartFile pdfFile) {

        String userId = getAuthenticatedUserId();
        try {
            CertificateDTO createdCertificate = certificateService.addCertificate(userId, certificateName, issuedBy, pdfFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificate);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //Delete a certificate (Ensure the user owns it)
    @DeleteMapping("/{certificateId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiResponse<String>> deleteCertificate(@PathVariable Integer certificateId) {
        String userId = getAuthenticatedUserId();  // ✅ Fetch userId from token email

        try {
            certificateService.deleteCertificateById(userId, certificateId);
            ApiResponse<String> response = new ApiResponse<>(200, "Certificate deleted successfully", null, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
