package com.example.task.Mapper;

import com.example.task.DTO.UserProjectDTO;
import com.example.task.Entity.User;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;

public class UserProjectMapper {
    public static UserProjectDTO mapToUserProjectDTO(UserProject userProject) {
        return new UserProjectDTO(
                userProject.getId(),
                userProject.getUser().getId(),
                userProject.getProject().getId()
        );
    }

    public static UserProject mapToUserProject(UserProjectDTO userProjectDTO, User user, Project project) {
        return new UserProject(
                userProjectDTO.getId(),
                user,
                project
        );
    }
}
