package com.example.task.Service;

import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import com.example.task.Mapper.ProjectMapper;
import com.example.task.Repository.ProjectRepository;
import com.example.task.Repository.UserProjectRepository;
import com.example.task.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserProjectRepository userProjectRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectMapper projectMapper,UserProjectRepository userProjectRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
        this.userProjectRepository = userProjectRepository;
    }



    public ProjectDTO getProjectByProjectId(Integer projectId) {
        return projectRepository.findById(projectId)
                .filter(project -> project.getDeletedAt() == null)
                .map(projectMapper::toDTO)
                .orElse(null);
    }


    public ProjectDTO updateProject(Integer projectId, ProjectDTO projectDTO, UserDetails authenticatedUser) {

        String loggedInUserEmail = authenticatedUser.getUsername();

        User loggedInUser = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));


        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));


        boolean isAdmin = loggedInUser.getRole().getRoleName().equalsIgnoreCase("ADMIN");


        if (!isAdmin && !project.getMentor().getUserId().equals(loggedInUser.getUserId())) {
            throw new RuntimeException("You do not have permission to update this project.");
        }


        project.setProjectName(projectDTO.getProjectName());
        project.setDescription(projectDTO.getDescription());

        projectRepository.save(project);
        return projectMapper.toDTO(project);
    }


    public void deleteProject(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        if (project.getDeletedAt() != null) {
            throw new RuntimeException("Project is already deleted.");
        }


        project.setDeletedAt(LocalDateTime.now());
        projectRepository.save(project);
    }







    public List<ProjectDTO> getUserProjects(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with userId: " + userId);
        }

        User user = userOptional.get();

        List<Project> projects = projectRepository.findByMentorAndDeletedAtIsNull(user);


        projects.forEach(project -> {
            System.out.println("Project ID: " + project.getProjectId() + ", Deleted At: " + project.getDeletedAt());
        });

        return projects.stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }


    public ProjectDTO addUserProject(String userId, ProjectDTO projectDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() ->
                new RuntimeException("User not found with ID: " + userId)
        );

        Project project = projectMapper.toEntity(projectDTO, user);


        project.setMentor(user);

        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }


    public void deleteUserProject(String userId, Integer projectId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        Optional<Project> projectOptional = projectRepository.findByProjectIdAndMentor(projectId, userOptional.get());
        if (projectOptional.isEmpty()) {
            throw new RuntimeException("Project not found for user ID: " + userId);
        }

        Project project = projectOptional.get();

        if (project.getDeletedAt() != null) {
            throw new RuntimeException("Project is already deleted.");
        }

        project.setDeletedAt(LocalDateTime.now());
        projectRepository.save(project);
    }



    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findByDeletedAtIsNull();
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {

        boolean projectExists = projectRepository.existsByProjectName(projectDTO.getProjectName());

        if (projectExists) {
            throw new RuntimeException("Project with name '" + projectDTO.getProjectName() + "' already exists.");
        }


        User mentor = userRepository.findById(projectDTO.getMentorId())
                .orElseThrow(() -> new RuntimeException("Mentor not found with ID: " + projectDTO.getMentorId()));


        Project project = projectMapper.toEntity(projectDTO, mentor);


        project = projectRepository.save(project);


        return projectMapper.toDTO(project);
    }





    public List<ProjectDTO> getProjectsByUserRole() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }


    public Integer getTotalProjectCount() {
        return (int) projectRepository.findByDeletedAtIsNull().size();
    }


}