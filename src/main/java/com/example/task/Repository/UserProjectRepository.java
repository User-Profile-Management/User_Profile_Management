package com.example.task.Repository;

import com.example.task.Entity.User;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT up FROM UserProject up WHERE up.user.id = :userId AND up.project.id = :projectId")
    Optional<UserProject> findByUserIdAndProjectId(@Param("userId") String userId, @Param("projectId") Integer projectId);


    List<UserProject> findByUserId(String userId);

    @Query("SELECT COUNT(up) FROM UserProject up " +
            "WHERE up.user.id = :userId AND up.status = :status")
    int countByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);
    @Query("SELECT u FROM UserProject u WHERE u.deletedAt IS NULL")
    List<UserProject> findAllActiveUserProjects();

    @Query("SELECT up.project FROM UserProject up WHERE up.userId = :studentId AND up.deletedAt IS NULL")
    List<Project> findProjectsByStudentId(@Param("studentId") String studentId);

    int countByStatus(String status);

}