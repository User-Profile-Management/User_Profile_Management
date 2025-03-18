package com.example.task.Controller;

import com.example.task.DTO.ProjectDTO;
import com.example.task.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Get count of completed projects for a user
    @GetMapping("/user/{userId}/projects/completed_count")
    public ResponseEntity<Integer> getCompletedProjectCount(@PathVariable String userId) {
        return ResponseEntity.ok(projectService.getCompletedProjectCount(userId));
    }

    // Get a specific project by ID
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Integer projectId) {
        ProjectDTO project = projectService.getProjectById(projectId);
        return project != null ? ResponseEntity.ok(project) : ResponseEntity.notFound().build();
    }

    // Edit project
    @PreAuthorize("hasAnyAuthority('MENTOR', 'ADMIN')")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Integer projectId,
            @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.updateProject(projectId, projectDTO));
    }

    // Delete a project
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Integer projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted successfully.");
    }

    // Get ongoing project count
    @GetMapping("/user/projects/ongoing_count")
    public ResponseEntity<Integer> getOngoingProjectCount() {
        return ResponseEntity.ok(projectService.getOngoingProjectCount());
    }

    // Get all projects for a user
    @GetMapping("/user/{userId}/projects")
    public ResponseEntity<List<ProjectDTO>> getUserProjects(@PathVariable Integer userId) {
        return ResponseEntity.ok(projectService.getUserProjects(String.valueOf(userId)));
    }

    // Add a project for a user
    @PostMapping("/user/{userId}/projects")
    public ResponseEntity<ProjectDTO> addUserProject(
            @PathVariable Integer userId,
            @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.addUserProject(String.valueOf(userId), projectDTO));
    }

    // Update project status for a user
    @PutMapping("/user/{userId}/projects/{projectId}")
    public ResponseEntity<ProjectDTO> updateUserProjectStatus(
            @PathVariable Integer userId,
            @PathVariable Integer projectId,
            @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.updateUserProjectStatus(String.valueOf(userId), projectId, projectDTO));
    }

    // Delete a project assigned to a user
    @DeleteMapping("/user/{userId}/projects/{projectId}")
    public ResponseEntity<String> deleteUserProject(
            @PathVariable Integer userId,
            @PathVariable Integer projectId) {
        projectService.deleteUserProject(String.valueOf(userId), projectId);
        return ResponseEntity.ok("Project deleted successfully for user.");
    }

    // Get all projects
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // Add a new project
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.createProject(projectDTO));
    }

    // Get projects based on user role
    @GetMapping("/user/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsByUserRole() {
        return ResponseEntity.ok(projectService.getProjectsByUserRole());
    }

    // Get total count of all projects
    @GetMapping("/user/projects/count")
    public ResponseEntity<Integer> getTotalProjectCount() {
        return ResponseEntity.ok(projectService.getTotalProjectCount());
    }

    // Get count of completed projects
    @GetMapping("/user/projects/completed_count")
    public ResponseEntity<Integer> getCompletedProjectsCount() {
        return ResponseEntity.ok(projectService.getCompletedProjectsCount());
    }
}