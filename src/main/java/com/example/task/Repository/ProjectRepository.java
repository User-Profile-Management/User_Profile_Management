package com.example.task.Repository;

import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    // Find all projects assigned to a specific mentor (User)
    List<Project> findByMentor(User mentor);

    // Find a project by its ID and Mentor (for user-specific project operations)
    Optional<Project> findByIdAndMentor(Integer projectId, User mentor);

    // Count the number of projects completed by a specific mentor
    Integer countByMentorAndStatus(User mentor, String status);

    // Count projects based on their status
    Integer countByStatus(String status);
}