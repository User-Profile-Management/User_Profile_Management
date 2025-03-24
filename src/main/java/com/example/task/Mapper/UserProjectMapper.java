package com.example.task.Mapper;

import com.example.task.DTO.ProfileDTO;
import com.example.task.DTO.ProjectDTO;
import com.example.task.DTO.UserProjectDTO;
import com.example.task.Entity.UserProject;
import org.springframework.stereotype.Component;

@Component
public class UserProjectMapper {
    public static UserProjectDTO mapToUserProjectDTO(UserProject userProject) {
        return new UserProjectDTO(
                userProject.getId(),
                new ProfileDTO(
                        userProject.getUser().getUserId(),
                        userProject.getUser().getFullName(),
                        userProject.getUser().getEmail(),
                        userProject.getUser().getContactNo(),
                        userProject.getUser().getAddress(),
                        userProject.getUser().getDateOfBirth(),
                        userProject.getUser().getStatus().name(),  // Convert Enum to String
                        userProject.getUser().getProfilePicture(),
                        userProject.getUser().getRole().getRoleName()
                ),
                new ProjectDTO(
                        userProject.getProject().getProjectId(),
                        userProject.getProject().getProjectName(),
                        userProject.getProject().getDescription(),
                        userProject.getProject().getMentor() != null ? userProject.getProject().getMentor().getUserId() : null, // ✅ FIXED: Now fetches mentor's ID correctly
                        userProject.getProject().getDeletedAt()
                ),
                userProject.getStatus()
        );
    }
}
