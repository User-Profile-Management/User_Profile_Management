package com.example.task.DTO;

import java.time.LocalDate;
import java.util.Base64;

public class ProfileDTO {
    private String userId;
    private String fullName;
    private String email;
    private String contactNo;
    private String emergencyContact;
    private String address;
    private LocalDate dateOfBirth;
    private String status;
    private byte[] profilePicture;
    private String role;

    public ProfileDTO(String userId, String fullName, String email, String emergencyContact,String contactNo, String address,
                      LocalDate dateOfBirth, String status, byte[] profilePicture, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.contactNo = contactNo;
        this.emergencyContact= emergencyContact;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    // Corrected Getters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getContactNo() { return contactNo; }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getAddress() { return address; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getStatus() { return status; }
    public byte[] getProfilePicture() { return profilePicture; }  // FIXED: Returns byte[]
    public String getRole() { return role; }

    // Setters
    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    // Convert Base64 to byte[] before setting
    public void setProfilePictureBase64(String profilePictureBase64) {
        if (profilePictureBase64 != null) {
            this.profilePicture = Base64.getDecoder().decode(profilePictureBase64);
        }
    }

    // Get profile picture as Base64 string
    public String getProfilePictureBase64() {
        return (profilePicture != null) ? Base64.getEncoder().encodeToString(profilePicture) : null;
    }
}
