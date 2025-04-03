package com.example.task.Mapper;

import com.example.task.DTO.ProfileDTO;
import com.example.task.DTO.ProjectDTO;
import com.example.task.DTO.UserProjectDTO;
import com.example.task.Entity.UserProject;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class UserProjectMapper {
    public static UserProjectDTO mapToUserProjectDTO(UserProject userProject) {
        ProfileDTO profileDTO = new ProfileDTO(
                userProject.getUser().getUserId(),
                userProject.getUser().getFullName(),
                userProject.getUser().getEmail(),
                userProject.getUser().getContactNo(),
                userProject.getUser().getEmergencyContact(),
                userProject.getUser().getAddress(),
                userProject.getUser().getDateOfBirth(),
                userProject.getUser().getStatus().name(),
                null, // Default profile picture
                userProject.getUser().getRole().getRoleName()
        );

        // ✅ Convert profile picture to Base64 if it exists
        if (userProject.getUser().getProfilePicture() != null) {
            profileDTO.setProfilePictureBase64(
                    Base64.getEncoder().encodeToString(userProject.getUser().getProfilePicture())
            );
        }

        ProjectDTO projectDTO = new ProjectDTO(
                userProject.getProject().getProjectId(),
                userProject.getProject().getProjectName(),
                userProject.getProject().getDescription(),
                userProject.getProject().getMentor() != null ? userProject.getProject().getMentor().getUserId() : null,
                userProject.getProject().getDeletedAt()
        );

        return new UserProjectDTO(
                userProject.getId(),
                profileDTO,
                projectDTO,
                userProject.getStatus()
        );
    }

}
