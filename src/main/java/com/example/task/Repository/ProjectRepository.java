package com.example.task.Repository;

import com.example.task.Entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Projects,Integer> {
    Integer countByUserIdAndStatus(Integer userId, String status);
    Integer countByStatus(String status);
    List<Projects> findByUserId(Integer userId);
    Optional<Projects> findByIdAndUserId(Integer projectId, Integer userId);
}
