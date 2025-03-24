package com.example.task.Mapper;

import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    // Convert Project entity to ProjectDTO (Only returning mentor ID)
    public ProjectDTO toDTO(Project project) {
        return ProjectDTO.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .description(project.getDescription())
                .mentorId(project.getMentor() != null ? project.getMentor().getUserId() : null) // Use getMentor() for entity
                .build();
    }

    // Convert ProjectDTO to Project entity (Requires full User object)
    public Project toEntity(ProjectDTO dto, User mentor) {
        return Project.builder()
                .projectId(dto.getProjectId())
                .projectName(dto.getProjectName())
                .description(dto.getDescription())
                .mentor(mentor) // Attach full mentor entity
                .build();
    }
}
