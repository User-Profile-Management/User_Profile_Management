package com.example.task.Mapper;


import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Projects;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectDTO toDTO(Projects project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        dto.setUserId(project.getUser().getId());
        return dto;
    }

    public Projects toEntity(ProjectDTO dto) {
        Projects project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        return project;
    }
}