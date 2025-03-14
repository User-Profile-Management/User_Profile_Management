package com.example.task.Service;

import com.example.task.Entity.User;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;
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
    private final ProjectRepository projectRepository; // ✅ FIXED: Added missing repository

    @Autowired
    public UserProjectService(UserProjectRepository userProjectRepository,
                              UserRepository userRepository,
                              ProjectRepository projectRepository) {
        this.userProjectRepository = userProjectRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository; // ✅ FIXED: Corrected duplicate repository
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

    public UserProject addUserProject(UserProject userProject) {
        // Fetch user
        User user = userRepository.findById(userProject.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch project
        Project project = projectRepository.findById(userProject.getProjectId()) // ✅ FIXED
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if the user is already assigned to the project
        if (userProjectRepository.findByUserIdAndProjectId(user.getUserId(), project.getId()).isPresent()) {
            throw new RuntimeException("User is already assigned to this project.");
        }

        // Set associations
        userProject.setUser(user);
        userProject.setProject(project);

        return userProjectRepository.save(userProject);
    }

    @Transactional
    public void updateUserProjectStatus(String userId, Integer projectId, String status) {
        UserProject userProject = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new RuntimeException("User is not assigned to this project"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ FIXED: Ensure `getRole()` returns a String or Enum
        if (!"MENTOR".equals(user.getRole().toString())) {
            throw new RuntimeException("Only mentors can update project status");
        }

        userProject.setStatus(status);
        userProjectRepository.save(userProject);
    }

    @Transactional
    public void softDeleteUserProjects(String userId) {
        List<UserProject> userProjects = userProjectRepository.findByUserId(userId);
        userProjects.forEach(userProject -> userProject.setDeletedAt(LocalDateTime.now()));
        userProjectRepository.saveAll(userProjects);
    }
}
