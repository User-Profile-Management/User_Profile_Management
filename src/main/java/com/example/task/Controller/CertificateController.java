package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.CertificateDTO;
import com.example.task.Entity.User;
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
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final UserRepository userRepository;


    @GetMapping("/get")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiResponse<List<CertificateDTO>>> getCertificates() {
        String userId = getAuthenticatedUserId();
        List<CertificateDTO> certificates = certificateService.getCertificatesByUserId(userId);

        if (certificates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(204, "No certificates found", Collections.emptyList(), null));
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Certificates retrieved successfully", certificates, null));
    }



    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        return user.getUserId();
    }


    @GetMapping("/{certificateId}/download")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Integer certificateId) {
        byte[] pdfData = certificateService.getCertificatePdfById(certificateId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=certificate_" + certificateId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }


    @PostMapping(path = "/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiResponse<CertificateDTO>> addCertificate(
            @RequestParam("certificateName") String certificateName,
            @RequestParam("issuedBy") String issuedBy,
            @RequestParam("file") MultipartFile pdfFile) {

        try {

            if (!pdfFile.getContentType().equalsIgnoreCase("application/pdf")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Invalid file type", null, "Only PDF files are allowed"));
            }


            if (pdfFile.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(new ApiResponse<>(413, "File too large", null, "Maximum file size is 5MB"));
            }


            String userId = getAuthenticatedUserId();


            CertificateDTO response = certificateService.addCertificate(userId, certificateName, issuedBy, pdfFile);


            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Certificate uploaded successfully", response, null));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error processing file", null, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null, e.getMessage()));
        }
    }



    @DeleteMapping("/{certificateId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiResponse<String>> deleteCertificate(@PathVariable Integer certificateId) {
        String userId = getAuthenticatedUserId();

        try {
            certificateService.deleteCertificateById(userId, certificateId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Certificate deleted successfully", null, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null, null));
        }
    }
}