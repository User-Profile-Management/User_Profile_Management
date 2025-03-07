package com.example.task.Service;

import com.example.task.Entity.User;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;
import com.example.task.Repository.UserProjectRepository;
import com.example.task.Repository.UserRepository;
import com.example.task.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public UserProject addUserProject(UserProject userProject) {
        return userProjectRepository.save(userProject);
    }

    public void deleteUserProject(int id) {
        userProjectRepository.deleteById(id);
    }
}
