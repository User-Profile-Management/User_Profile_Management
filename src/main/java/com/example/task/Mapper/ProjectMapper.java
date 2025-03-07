package com.example.task.Mapper;


import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectDTO toDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        dto.setMentorId(project.getMentor() != null ? project.getMentor().getUserId() : null);
        return dto;
    }

    public Project toEntity(ProjectDTO dto, User mentor) {
        Project project = new Project();
        project.setId(dto.getId());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setMentor(mentor);
        return project;
    }

    public Project toEntity(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectName(projectDTO.getProjectName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        return project;
    }
}