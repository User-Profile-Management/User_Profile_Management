package com.example.task.Service;

import com.example.task.DTO.ResponseDTO;
import com.example.task.Entity.User;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;
import com.example.task.Repository.UserProjectRepository;
import com.example.task.Repository.UserRepository;
import com.example.task.Repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserProjectService {
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public UserProjectService(UserProjectRepository userProjectRepository,
                              UserRepository userRepository,
                              ProjectRepository projectRepository) {
        this.userProjectRepository = userProjectRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public List<UserProject> getAllUserProjects() {
        return userProjectRepository.findAll();
    }

    public List<UserProject> getUserProjectsByUserId(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(userProjectRepository::findByUser).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserProject> getUsersByProjectId(int projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        return project.map(userProjectRepository::findByProject).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public ResponseDTO<UserProject>  addUserProject(UserProject userProject) {
        // Fetch user from database
        User user = userRepository.findById(userProject.getUserId())
                .orElse(null);
        if (user == null) {
            return ResponseDTO.error(404, "User not found");
        }

        // Fetch project from database
        Project project = projectRepository.findById(userProject.getProjectId())
                .orElse(null);
        if (project == null) {
            return ResponseDTO.error(404, "Project not found");
        }

        // Check if the user is already assigned to the same project
        Optional<UserProject> existingAssignment = userProjectRepository
                .findByUserIdAndProjectId(user.getUserId(), project.getId());

        if (existingAssignment.isPresent()) {
            return ResponseDTO.error(409, "User is already assigned to this project.");
        }

        // Set user and project before saving
        userProject.setUser(user);
        userProject.setProject(project);

        UserProject savedProject = userProjectRepository.save(userProject);
        return ResponseDTO.success("User project assigned successfully.", savedProject);
    }

    @Transactional
    public void softDeleteUserProjects(String userId) {
        List<UserProject> userProjects = userProjectRepository.findByUserId(userId);
        userProjects.forEach(userProject -> userProject.setDeletedAt(LocalDateTime.now()));
        userProjectRepository.saveAll(userProjects);
    }
}

