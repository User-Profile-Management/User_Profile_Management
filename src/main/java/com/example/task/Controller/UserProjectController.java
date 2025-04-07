package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.UserProjectDTO;
import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import com.example.task.Entity.UserProject;
import com.example.task.Repository.ProjectRepository;
import com.example.task.Repository.UserProjectRepository;
import com.example.task.Repository.UserRepository;
import com.example.task.Service.ProjectService;
import com.example.task.Service.UserProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


import java.util.List;

@RestController
@RequestMapping("/api/user-projects")
public class UserProjectController {
    private final UserProjectService userProjectService;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    @Autowired
    public UserProjectController(UserProjectService userProjectService,ProjectRepository projectRepository,ProjectService projectService,UserRepository userRepository) {
        this.userProjectService = userProjectService;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProject>>> getAllUserProjects() {
        List<UserProject> userProjects = userProjectService.getAllUserProjects();
        return ResponseEntity.ok(new ApiResponse(200, "Fetched all user projects", userProjects, null));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MENTOR','STUDENT') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> getAssignedProjects(@PathVariable String userId, Authentication authentication) {
        List<Project> projects = userProjectService.getAssignedProjects(userId);
        return ResponseEntity.ok(new ApiResponse(200, "Assigned projects fetched successfully", projects, null));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<UserProjectDTO>> addUserProject(@RequestBody UserProject userProject,@AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        String mentorId;

        try {
            mentorId = userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElseThrow(() -> new UsernameNotFoundException("Mentor not found: " + email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Internal Server Error", null, "Could not retrieve mentor ID"));
        }


        // Step 1: Check if the project exists
        Project project = projectService.getProjectById(userProject.getProjectId());
        if (project == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Project not found", null, "The specified project does not exist"));
        }

        // Step 2: Check if the mentor is assigned to the project
        boolean isMentorAssigned = projectService.isMentorAssignedToProject(mentorId, userProject.getProjectId());
        if (!isMentorAssigned) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Access Denied", null, "Mentor is not assigned to this project"));
        }

        // Step 3: Proceed with adding the user to the project
        UserProjectDTO createdUserProject = userProjectService.addUserProject(userProject);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "User project added successfully", createdUserProject, null));
    }
    @PutMapping("/users/{userId}/projects/{projectId}")
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<String>> updateUserProjectStatus(
            @PathVariable String userId,
            @PathVariable Integer projectId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails authenticatedUser) {

        String email = authenticatedUser.getUsername();
        String mentorId;

        try {
            mentorId = userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElseThrow(() -> new UsernameNotFoundException("Mentor not found: " + email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Internal Server Error", null, "Could not retrieve mentor ID"));
        }

        String status = request.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Bad Request", null, "Status is required"));
        }

        // Step 1: Check if the project exists
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Project not found", null, "The specified project does not exist"));
        }

        // Step 2: Check if the mentor is assigned to the project
        boolean isMentorAssigned = projectService.isMentorAssignedToProject(mentorId, projectId);
        if (!isMentorAssigned) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Access Denied", null, "Mentor is not assigned to this project"));
        }

        // Step 3: Proceed to update user project status
        try {
            userProjectService.updateUserProjectStatus(userId, projectId, status);
            return ResponseEntity.ok(new ApiResponse<>(200, "Project status updated successfully", "Status: " + status, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to update status", null, e.getMessage()));
        }
    }








}


