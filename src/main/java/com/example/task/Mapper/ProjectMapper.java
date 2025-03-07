package com.example.task.Mapper;


import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectDTO toDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        dto.setId(Integer.parseInt(project.getUserId()));
        return dto;
    }

    public Project toEntity(ProjectDTO dto) {
        Project project = new Project();
        project.setId(dto.getId());
        project.setProjectName(dto.getProjectName()); // Fix here
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        return project;
    }
}