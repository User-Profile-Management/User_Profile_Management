package com.example.task.Repository;

import com.example.task.Entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Integer> {
    List<UserProject> findByUserId(String userId);
    List<UserProject> findByProjectId(int projectId);
}
