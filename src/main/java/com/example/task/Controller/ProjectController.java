package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.ProjectDTO;
import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import com.example.task.Repository.ProjectRepository;
import com.example.task.Repository.UserProjectRepository;
import com.example.task.Repository.UserRepository;
import com.example.task.Service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectService projectService, UserRepository userRepository,UserProjectRepository userProjectRepository,ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.userProjectRepository = userProjectRepository;
        this.projectRepository = projectRepository;
    }



    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(@PathVariable Integer projectId) {
        ProjectDTO project = projectService.getProjectByProjectId(projectId);
        if (project != null) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Project found", project,null));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse<>(404, "Project not found", null,null));
        }
    }

    @PreAuthorize("hasAnyAuthority('MENTOR', 'ADMIN')")
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable Integer projectId,
            @RequestBody ProjectDTO projectDTO,
            @AuthenticationPrincipal UserDetails authenticatedUser) {

        try {
            // Check if authenticatedUser is null
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "User is not authenticated", null, "Unauthorized"));
            }

            String loggedInUserEmail = authenticatedUser.getUsername();

            // Fetch user by email
            User loggedInUser = userRepository.findByEmail(loggedInUserEmail)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB"));

            // Check if project exists
            Project existingProject = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

            // Check if the project is deleted
            if (existingProject.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Project is deleted and cannot be updated", null, null));
            }

            // Check if the user is an admin
            boolean isAdmin = loggedInUser.getRole() != null &&
                    loggedInUser.getRole().getRoleName().equalsIgnoreCase("ADMIN");

            // Admin can edit any project, but mentors can only edit their own projects
            if (!isAdmin && (existingProject.getMentor() == null ||
                    !existingProject.getMentor().getUserId().equals(loggedInUser.getUserId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(403, "You do not have permission to update this project.", null, "Access Denied"));
            }

            // Update the project
            ProjectDTO updatedProject = projectService.updateProject(projectId, projectDTO, authenticatedUser);

            return ResponseEntity.ok(new ApiResponse<>(200, "Project updated successfully", updatedProject, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error updating project: " + e.getMessage(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Integer projectId) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Project deleted successfully.", null, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null, "Project not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error deleting project: " + e.getMessage(), null, e.getMessage()));
        }
    }








    @GetMapping("/user/{userId}/projects")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getUserProjects(@PathVariable String userId) {
        List<ProjectDTO> userProjects = projectService.getUserProjects(String.valueOf(userId));

        if (userProjects != null && !userProjects.isEmpty()) {
            ApiResponse<List<ProjectDTO>> response = new ApiResponse<>(200, "Projects fetched successfully", userProjects, null);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<List<ProjectDTO>> response = new ApiResponse<>(404, "No projects found for the user", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }








    @DeleteMapping("/users/{userId}/projects/{projectId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUserProject(
            @PathVariable String userId,
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


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@RequestBody ProjectDTO projectDTO) {
        try {

            if (projectDTO.getMentorId() == null) {
                throw new RuntimeException("Mentor ID is required.");
            }


            User mentor = userRepository.findById(projectDTO.getMentorId())
                    .orElseThrow(() -> new RuntimeException("Mentor not found with ID: " + projectDTO.getMentorId()));


            if (!"MENTOR".equalsIgnoreCase(mentor.getRole().getRoleName())) {
                throw new RuntimeException("User " + mentor.getUserId() + " is not a mentor.");
            }


            if (!"ACTIVE".equalsIgnoreCase(mentor.getStatus().name()) || mentor.getDeletedAt() != null) {
                throw new RuntimeException("Mentor " + mentor.getUserId() + " is inactive or deleted.");
            }


            if (userProjectRepository.findByUserIdAndProjectId(mentor.getUserId(), projectDTO.getProjectId()).isPresent()) {
                throw new RuntimeException("Mentor is already assigned to this project.");
            }


            projectDTO.setMentorId(mentor.getUserId());


            ProjectDTO createdProject = projectService.createProject(projectDTO);


            ApiResponse<ProjectDTO> response = new ApiResponse<>(201, "Project created successfully", createdProject, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponse<ProjectDTO> response = new ApiResponse<>(500, "Error creating project: " + e.getMessage(), null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }






    @GetMapping("/user/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsByUserRole() {
        return ResponseEntity.ok(projectService.getProjectsByUserRole());
    }


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



}
