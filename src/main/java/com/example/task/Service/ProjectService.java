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

//    // Get count of completed projects for a user
//    public Integer getCompletedProjectCount(String userId) {
//        Optional<User> userOptional = userRepository.findById(userId);
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            return projectRepository.countByMentorAndStatus(user, "COMPLETED");
//        } else {
//            throw new RuntimeException("User not found with userId: " + userId);
//        }
//    }

    public ProjectDTO getProjectByProjectId(Integer projectId) {
        return projectRepository.findById(projectId)
                .filter(project -> project.getDeletedAt() == null) // Check if the project is not deleted
                .map(projectMapper::toDTO)  // Convert to DTO
                .orElse(null); // Return null if the project is deleted or not found
    }


    public ProjectDTO updateProject(Integer projectId, ProjectDTO projectDTO, UserDetails authenticatedUser) {
        // Get logged-in user's email from the token
        String loggedInUserEmail = authenticatedUser.getUsername();  // Assuming email is stored as username

        // Fetch the logged-in user
        User loggedInUser = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // Fetch the project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Check if the user is an ADMIN (Admins can update any project)
        boolean isAdmin = loggedInUser.getRole().getRoleName().equalsIgnoreCase("ADMIN");

        // Ensure the mentor updating the project is the assigned mentor
        if (!isAdmin && !project.getMentor().getUserId().equals(loggedInUser.getUserId())) {
            throw new RuntimeException("You do not have permission to update this project.");
        }

        // Proceed with updating project details
        project.setProjectName(projectDTO.getProjectName());
        project.setDescription(projectDTO.getDescription());

        projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    // Delete a project
    public void deleteProject(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        if (project.getDeletedAt() != null) {
            throw new RuntimeException("Project is already deleted.");
        }

        // Soft delete by setting the deleted timestamp
        project.setDeletedAt(LocalDateTime.now());
        projectRepository.save(project);
    }



//    // Get ongoing project count
//    public Integer getOngoingProjectCount() {
//        return userProjectRepository.countByStatus("ONGOING");
//    }



    // Get all projects for a user
    public List<ProjectDTO> getUserProjects(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with userId: " + userId);
        }

        User user = userOptional.get();
        // Fetch the user's projects but exclude projects where deletedAt is not null
        List<Project> projects = projectRepository.findByMentorAndDeletedAtIsNull(user);

        // Log to check the deletedAt value for each project (for debugging purposes)
        projects.forEach(project -> {
            System.out.println("Project ID: " + project.getProjectId() + ", Deleted At: " + project.getDeletedAt());
        });

        return projects.stream()
                .map(projectMapper::toDTO)  // Map to DTO
                .collect(Collectors.toList()); // Collect the result into a list
    }


    public ProjectDTO addUserProject(String userId, ProjectDTO projectDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() ->
                new RuntimeException("User not found with ID: " + userId)
        );

        Project project = projectMapper.toEntity(projectDTO, user);

        // ✅ FIX: Use setMentor(user) instead of setMentorId(user.getUserId())
        project.setMentor(user);

        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

//    // Update project status for a user
//    public ProjectDTO updateUserProjectStatus(String userId, Integer projectId, ProjectDTO projectDTO) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (!userOptional.isPresent()) {
//            throw new RuntimeException("User not found with ID: " + userId);
//        }
//
//        Optional<Project> projectOptional = projectRepository.findByIdAndMentor(projectId, userOptional.get());
//        if (!projectOptional.isPresent()) {
//            throw new RuntimeException("Project not found for user ID: " + userId);
//        }
//
//        Project project = projectOptional.get();
//
//        projectRepository.save(project);
//        return projectMapper.toDTO(project);
//    }

    // Delete a project assigned to a user
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

        project.setDeletedAt(LocalDateTime.now()); // Mark as deleted
        projectRepository.save(project);
    }


    // Get all projects
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findByDeletedAtIsNull();
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        // Check if a project with the same name already exists across all mentors
        boolean projectExists = projectRepository.existsByProjectName(projectDTO.getProjectName());

        if (projectExists) {
            throw new RuntimeException("Project with name '" + projectDTO.getProjectName() + "' already exists.");
        }

        // Fetch the mentor using the mentorId
        User mentor = userRepository.findById(projectDTO.getMentorId())
                .orElseThrow(() -> new RuntimeException("Mentor not found with ID: " + projectDTO.getMentorId()));

        // Convert DTO to Entity with the fetched mentor
        Project project = projectMapper.toEntity(projectDTO, mentor);

        // Save the project
        project = projectRepository.save(project);

        // Return the saved project as a DTO
        return projectMapper.toDTO(project);
    }




    // Get projects based on user role
    public List<ProjectDTO> getProjectsByUserRole() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // Get total count of all projects
    public Integer getTotalProjectCount() {
        return (int) projectRepository.findByDeletedAtIsNull().size();
    }

//    // Get count of completed projects
//    public Integer getCompletedProjectsCount() {
//        return projectRepository.countByStatus("COMPLETED");
//    }
}