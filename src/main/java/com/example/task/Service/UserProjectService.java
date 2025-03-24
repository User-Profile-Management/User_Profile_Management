package com.example.task.Service;

import com.example.task.DTO.ProfileDTO;
import com.example.task.DTO.ProjectDTO;
import com.example.task.DTO.UserProjectDTO;
import com.example.task.Entity.User;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;
import com.example.task.Mapper.UserProjectMapper;
import com.example.task.Repository.UserProjectRepository;
import com.example.task.Repository.UserRepository;
import com.example.task.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserProjectService {
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectMapper userProjectMapper;// ✅ FIXED: Added missing repository

    @Autowired
    public UserProjectService(UserProjectRepository userProjectRepository,
                              UserRepository userRepository,
                              ProjectRepository projectRepository,UserProjectMapper userProjectMapper) {
        this.userProjectRepository = userProjectRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository; // FIXED: Corrected duplicate repository
        this.userProjectMapper = userProjectMapper;
    }

    public List<UserProject> getAllUserProjects() {
        return userProjectRepository.findAll();
    }

    public List<UserProject> getUserProjectsByUserId(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(userProjectRepository::findByUser)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserProject> getUsersByProjectId(int projectId) {
        Optional<Project> project = projectRepository.findById(projectId); // ✅ FIXED
        return project.map(userProjectRepository::findByProject)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public UserProjectDTO addUserProject(UserProject userProject) {
        // Fetch user
        User user = userRepository.findById(userProject.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + userProject.getUserId()));

        // Ensure user is ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus().name())) {
            throw new RuntimeException("User " + user.getUserId() + " is not active. Cannot assign project.");
        }

        // Fetch project
        Project project = projectRepository.findById(userProject.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found: " + userProject.getProjectId()));

        // Prevent duplicate assignment
        if (userProjectRepository.findByUserIdAndProjectId(user.getUserId(), project.getProjectId()).isPresent()) {
            throw new RuntimeException("User is already assigned to this project.");
        }

        // Ensure status is not null
        if (userProject.getStatus() == null) {
            throw new RuntimeException("Project status cannot be null.");
        }

        // Set associations
        userProject.setUser(user);
        userProject.setProject(project);

        try {
            userProject.setUserId(user.getUserId());
            userProject.setProjectId(project.getProjectId());

            UserProject savedUserProject = userProjectRepository.save(userProject);

            // Use the mapper for conversion
            return UserProjectMapper.mapToUserProjectDTO(savedUserProject);
        } catch (Exception e) {
            throw new RuntimeException("Error saving UserProject: " + e.getMessage(), e);
        }
    }






    public List<Project> getAssignedProjects(String studentId) {
        // Fetch only non-deleted projects assigned to the student
        return userProjectRepository.findProjectsByStudentId(studentId);
    }
    public void softDeleteUserProject(int userProjectId) {
        Optional<UserProject> userProject = userProjectRepository.findById(userProjectId);
        if (userProject.isPresent()) {
            userProject.get().setDeletedAt(LocalDateTime.now());
            userProjectRepository.save(userProject.get());
        } else {
            throw new RuntimeException("UserProject not found");
        }
    }

    @Transactional
    public void updateUserProjectStatus(String userId, Integer projectId, String status) {
        System.out.println("Updating project status for user: " + userId + ", project: " + projectId);

        UserProject userProject = userProjectRepository.findByUserUserIdAndProjectProjectId(userId, projectId)
                .orElseThrow(() -> new RuntimeException("User is not assigned to this project"));

        System.out.println("Found UserProject: " + userProject.getId() + " with current status: " + userProject.getStatus());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔍 Debugging: Print user role before checking
        System.out.println("User role found: " + user.getRole().getRoleName());



        System.out.println("Changing status to: " + status);
        userProject.setStatus(status);
        userProjectRepository.save(userProject);
        System.out.println("Updated status: " + userProject.getStatus());
    }

}
