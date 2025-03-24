package com.example.task.Repository;

import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import com.example.task.Entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {


    Optional<Project> findByProjectIdAndMentor(Integer projectId, User mentor);










    List<Project> findByDeletedAtIsNull();


    List<Project> findByMentorAndDeletedAtIsNull(User mentor);
    boolean existsByProjectName(String projectName);


    Optional<Project> findByProjectNameAndMentor(String projectName, User mentor);
    Optional<Project> findByProjectName(String projectName);

}
