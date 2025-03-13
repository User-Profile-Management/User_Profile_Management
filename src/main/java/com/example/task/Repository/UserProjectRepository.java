package com.example.task.Repository;

import com.example.task.Entity.User;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Integer> {
    // Fetch all projects assigned to a specific user
    List<UserProject> findByUser(User user);

    // Fetch all users assigned to a specific project
    List<UserProject> findByProject(Project project);
    Optional<UserProject> findByUserAndProject(User user, Project project);

    Optional<UserProject> findByUserIdAndProjectId(String userId, int Id);

    List<UserProject> findByUserId(String userId);
}