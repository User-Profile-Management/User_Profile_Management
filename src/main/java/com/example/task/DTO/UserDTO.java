package com.example.task.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long userId;
    private String googleId;
    private String fullName;
    private String emergencyContact;
    private LocalDate dateOfBirth;
    private String contactNo;
    private String address;
    private String status;
    private String profilePicture;
    private String email;

    private String roleName;  // Instead of Role entity, we use role name
    private Set<String> projectNames;  // List of project names
    private List<String> certificateNames;  // List of certificate titles
    private Set<String> badgeNames;  // List of badge names
}