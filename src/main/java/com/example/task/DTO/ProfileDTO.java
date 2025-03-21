package com.example.task.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;



public class ProfileDTO {
    private String userId;
    private String fullName;
    private String email;
    private String contactNo;
    private String address;
    private LocalDate dateOfBirth;
    private String status;
    private String profilePicture;
    private String role;

    public ProfileDTO(String userId, String fullName, String email, String contactNo, String address,
                      LocalDate dateOfBirth, String status, String profilePicture, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.contactNo = contactNo;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    //Ensure all fields have getters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getContactNo() { return contactNo; }
    public String getAddress() { return address; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getStatus() { return status; }
    public String getProfilePicture() { return profilePicture; }
    public String getRole() { return role; }
}




