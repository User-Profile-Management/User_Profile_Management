package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.ProjectDTO;
import com.example.task.Service.ProjectService;
import org.springframework.http.HttpStatus;
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

//    // Get count of completed projects for a user
//    @GetMapping("/user/{userId}/projects/completed_count")
//    public ResponseEntity<Integer> getCompletedProjectCount(@PathVariable String userId) {
//        return ResponseEntity.ok(projectService.getCompletedProjectCount(userId));
//    }

    // Edit project for everyone
    // Get a specific project by ID
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(@PathVariable Integer projectId) {
        ProjectDTO project = projectService.getProjectById(projectId);
        if (project != null) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Project found", project,null));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse<>(404, "Project not found", null,null));
        }
    }

    // Edit project
    @PreAuthorize("hasAnyAuthority('MENTOR', 'ADMIN')")
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable Integer projectId,
            @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(projectId, projectDTO);

        if (updatedProject != null) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Project updated successfully", updatedProject,null));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse<>(404, "Project not found", null,null));
        }
    }

    // Delete a project
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Integer projectId) {
        projectService.deleteProject(projectId);
        ApiResponse<String> response = new ApiResponse<>(200, "Project deleted successfully.", null, null);
        return ResponseEntity.ok(response);
    }



//    // Get ongoing project count
//    @GetMapping("/user/projects/ongoing_count")
//    public ResponseEntity<Integer> getOngoingProjectCount() {
//        return ResponseEntity.ok(projectService.getOngoingProjectCount());
//    }

    // Get all projects for a user
    @GetMapping("/user/{userId}/projects")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getUserProjects(@PathVariable Integer userId) {
        List<ProjectDTO> userProjects = projectService.getUserProjects(String.valueOf(userId));

        if (userProjects != null && !userProjects.isEmpty()) {
            ApiResponse<List<ProjectDTO>> response = new ApiResponse<>(200, "Projects fetched successfully", userProjects, null);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<List<ProjectDTO>> response = new ApiResponse<>(404, "No projects found for the user", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    // Add a project for a user
    @PostMapping("/user/{userId}/projects")
    public ResponseEntity<ApiResponse<ProjectDTO>> addUserProject(
            @PathVariable Integer userId,
            @RequestBody ProjectDTO projectDTO) {

        try {
            ProjectDTO createdProject = projectService.addUserProject(String.valueOf(userId), projectDTO);

            ApiResponse<ProjectDTO> response = new ApiResponse<>(201, "Project added successfully", createdProject, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponse<ProjectDTO> response = new ApiResponse<>(500, "Error adding project: " + e.getMessage(), null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




    // Delete a project assigned to a user
    @DeleteMapping("/user/{userId}/projects/{projectId}")
    public ResponseEntity<ApiResponse<String>> deleteUserProject(
            @PathVariable Integer userId,
            @PathVariable Integer projectId) {
        try {
            projectService.deleteUserProject(String.valueOf(userId), projectId);

            ApiResponse<String> response = new ApiResponse<>(200, "Project deleted successfully for user", "Project ID: " + projectId, null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(500, "Error deleting project: " + e.getMessage(), null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get all projects
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects() {
        try {
            List<ProjectDTO> projects = projectService.getAllProjects();
            ApiResponse<List<ProjectDTO>> response = new ApiResponse<>(200, "Fetched all projects successfully", projects, null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<ProjectDTO>> response = new ApiResponse<>(500, "Error fetching projects: " + e.getMessage(), null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Add a new project
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@RequestBody ProjectDTO projectDTO) {
        try {
            ProjectDTO createdProject = projectService.createProject(projectDTO);
            ApiResponse<ProjectDTO> response = new ApiResponse<>(201, "Project created successfully", createdProject, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponse<ProjectDTO> response = new ApiResponse<>(500, "Error creating project: " + e.getMessage(), null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Get projects based on user role
    @GetMapping("/user/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsByUserRole() {
        return ResponseEntity.ok(projectService.getProjectsByUserRole());
    }

    // Get total count of all projects
    @GetMapping("/user/projects/count")
    public ResponseEntity<ApiResponse<Integer>> getTotalProjectCount() {
        try {
            Integer totalCount = projectService.getTotalProjectCount();
            ApiResponse<Integer> response = new ApiResponse<>(200, "Total project count fetched successfully", totalCount, null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Integer> response = new ApiResponse<>(500, "Error fetching total project count: " + e.getMessage(), null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


//    // Get count of completed projects
//    @GetMapping("/user/projects/completed_count")
//    public ResponseEntity<Integer> getCompletedProjectsCount() {
//        return ResponseEntity.ok(projectService.getCompletedProjectsCount());
//    }
}