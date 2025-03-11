package com.example.task.Mapper;

import com.example.task.DTO.UserProjectDTO;
import com.example.task.Entity.User;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;

public class UserProjectMapper {
    public static UserProjectDTO mapToUserProjectDTO(UserProject userProject) {
        return new UserProjectDTO(
                userProject.getId(),  // Ensure ID is an int
                userProject.getUser().getUserId(),  // Ensure userId is String
                userProject.getProject().getId() // Ensure projectId is int
        );
    }

    public static UserProject mapToUserProject(UserProjectDTO userProjectDTO, User user, Project project) {
        UserProject userProject = new UserProject();

        // If UserProject has an 'id' field
        userProject.setId(userProjectDTO.getId());

        userProject.setUser(user);
        userProject.setProject(project);

        return userProject;
    }
}
