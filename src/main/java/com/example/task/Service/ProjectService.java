package com.example.task.Service;

import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import com.example.task.Mapper.ProjectMapper;
import com.example.task.Repository.ProjectRepository;
import com.example.task.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    public Integer getCompletedProjectCount(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return projectRepository.countByMentorAndStatus(user, "COMPLETED");
        } else {
            throw new RuntimeException("User not found with userId: " + userId);
        }
    }

    // Edit a project
    public ProjectDTO updateProject(Integer projectId, ProjectDTO projectDTO) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new RuntimeException("Project not found with ID: " + projectId);
        }

        Project project = projectOptional.get();
        project.setProjectName(projectDTO.getProjectName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());

        projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Delete a project
    public void deleteProject(Integer projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            throw new RuntimeException("Project not found with ID: " + projectId);
        }

        Project project = projectOptional.get();

        if (project.getDeletedAt() != null) {
            throw new RuntimeException("Project is already deleted.");
        }

        project.setDeletedAt(LocalDateTime.now()); // Mark project as deleted
        projectRepository.save(project);
    }

    // Get ongoing project count
    public Integer getOngoingProjectCount() {
        return projectRepository.countByStatus("ONGOING");
    }

    // Get all projects for a user
    public List<ProjectDTO> getUserProjects(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with userId: " + userId);
        }

        User user = userOptional.get();
        return projectRepository.findByMentorAndDeletedAtIsNull(user).stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Add a project for a user
    public ProjectDTO addUserProject(String userId, ProjectDTO projectDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        Project project = projectMapper.toEntity(projectDTO, userOptional.get());
        project.setMentor(userOptional.get()); // Assign mentor
        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Update project status for a user
    public ProjectDTO updateUserProjectStatus(String userId, Integer projectId, ProjectDTO projectDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        Optional<Project> projectOptional = projectRepository.findByIdAndMentor(projectId, userOptional.get());
        if (!projectOptional.isPresent()) {
            throw new RuntimeException("Project not found for user ID: " + userId);
        }

        Project project = projectOptional.get();
        project.setStatus(projectDTO.getStatus());
        projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Delete a project assigned to a user
    public void deleteUserProject(String userId, Integer projectId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        Optional<Project> projectOptional = projectRepository.findByIdAndMentor(projectId, userOptional.get());
        if (projectOptional.isEmpty()) {
            throw new RuntimeException("Project not found for user ID: " + userId);
        }

        Project project = projectOptional.get();

        if (project.getDeletedAt() != null) {
            throw new RuntimeException("Project is already deleted.");
        }

        project.setDeletedAt(LocalDateTime.now()); // Mark as deleted
        projectRepository.save(project);
    }


    // Get all projects
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findByDeletedAtIsNull();
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Optional<User> mentorOptional = userRepository.findById(projectDTO.getMentorId());

        if (!mentorOptional.isPresent()) {
            throw new RuntimeException("Mentor not found with ID: " + projectDTO.getMentorId());
        }

        Project project = projectMapper.toEntity(projectDTO, mentorOptional.get());
        project.setMentor(mentorOptional.get()); // Ensure mentor is set before saving

        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Get projects based on user role (returns all projects for now)
    public List<ProjectDTO> getProjectsByUserRole() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // Get total count of all projects
    public Integer getTotalProjectCount() {
        return (int) projectRepository.findByDeletedAtIsNull().size();
    }

    // Get count of completed projects
    public Integer getCompletedProjectsCount() {
        return projectRepository.countByStatus("COMPLETED");
    }
}