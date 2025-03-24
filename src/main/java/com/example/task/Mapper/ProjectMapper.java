package com.example.task.Mapper;

import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {


    public ProjectDTO toDTO(Project project) {
        return ProjectDTO.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .description(project.getDescription())
                .mentorId(project.getMentor() != null ? project.getMentor().getUserId() : null) // Use getMentor() for entity
                .build();
    }


    public Project toEntity(ProjectDTO dto, User mentor) {
        return Project.builder()
                .projectId(dto.getProjectId())
                .projectName(dto.getProjectName())
                .description(dto.getDescription())
                .mentor(mentor)
                .build();
    }
}
