package com.example.task.Repository;

import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    // Find a project by its ID and Mentor (for user-specific project operations)
    Optional<Project> findByIdAndMentor(Integer projectId, User mentor);

//    // Count the number of projects completed by a specific mentor
//    Integer countByMentorAndStatus(User mentor, String status);






    // Fetch only active projects
    List<Project> findByDeletedAtIsNull();

    // Fetch only active projects for a mentor
    List<Project> findByMentorAndDeletedAtIsNull(User mentor);

//    Integer countByStatus(String completed);
}