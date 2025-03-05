package com.example.task.Service;

import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Projects;
import com.example.task.Mapper.ProjectMapper;
import com.example.task.Repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
    }

    // Get count of completed projects for a user
    public Integer getCompletedProjectCount(Integer userId) {
        return projectRepository.countByUserIdAndStatus(userId, "COMPLETED");
    }

    // Edit a project
    public ProjectDTO updateProject(Integer projectId, ProjectDTO projectDTO) {
        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());

        projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Delete a project
    public void deleteProject(Integer projectId) {
        projectRepository.deleteById(projectId);
    }

    // Get ongoing project count
    public Integer getOngoingProjectCount() {
        return projectRepository.countByStatus("ONGOING");
    }

    // Get all projects for a user
    public List<ProjectDTO> getUserProjects(Integer userId) {
        List<Projects> projects = projectRepository.findByUserId(userId);
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // Add a project for a user
    public ProjectDTO addUserProject(Integer userId, ProjectDTO projectDTO) {
        Projects project = projectMapper.toEntity(projectDTO);
        project.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Update project status for a user
    public ProjectDTO updateUserProjectStatus(Integer userId, Integer projectId, ProjectDTO projectDTO) {
        Projects project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found for user"));

        project.setStatus(projectDTO.getStatus());
        projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Delete a project assigned to a user
    public void deleteUserProject(Integer userId, Integer projectId) {
        Projects project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found for user"));
        projectRepository.delete(project);
    }

    // Get all projects
    public List<ProjectDTO> getAllProjects() {
        List<Projects> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // Add a new project
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Projects project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Get projects based on user role (for now, returns all projects)
    public List<ProjectDTO> getProjectsByUserRole() {
        List<Projects> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // Get total count of all projects
    public Integer getTotalProjectCount() {
        return (int) projectRepository.count();
    }

    // Get count of completed projects
    public Integer getCompletedProjectsCount() {
        return projectRepository.countByStatus("COMPLETED");
    }
}
