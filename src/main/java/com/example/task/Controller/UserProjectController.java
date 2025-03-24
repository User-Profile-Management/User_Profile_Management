package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.UserProjectDTO;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;
import com.example.task.Repository.ProjectRepository;
import com.example.task.Repository.UserProjectRepository;
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
import org.springframework.web.bind.annotation.*;
import java.util.Map;


import java.util.List;

@RestController
@RequestMapping("/api/user-projects")
public class UserProjectController {
    private final UserProjectService userProjectService;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    @Autowired
    public UserProjectController(UserProjectService userProjectService,ProjectRepository projectRepository,ProjectService projectService) {
        this.userProjectService = userProjectService;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProject>>> getAllUserProjects() {
        List<UserProject> userProjects = userProjectService.getAllUserProjects();
        return ResponseEntity.ok(new ApiResponse(200, "Fetched all user projects", userProjects, null));
    }

    @GetMapping("/users/{studentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MENTOR') or #studentId == authentication.principal.id")
    public ResponseEntity<ApiResponse> getAssignedProjects(@PathVariable String studentId, Authentication authentication) {
        List<Project> projects = userProjectService.getAssignedProjects(studentId);
        return ResponseEntity.ok(new ApiResponse(200, "Assigned projects fetched successfully", projects, null));
    }


    @PutMapping("/users/{userId}/projects/{projectId}")
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse> updateUserProjectStatus(
            @PathVariable String userId,
            @PathVariable Integer projectId,
            @RequestParam Map<String, String> request,
            @AuthenticationPrincipal UserDetails authenticatedUser) {

        String status = request.get("status");
        String loggedInUserId = authenticatedUser.getUsername();


        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        if (!project.getMentor().getUserId().equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(403, "You do not have permission to update this project status.", null, "Access Denied"));
        }


        userProjectService.updateUserProjectStatus(userId, projectId, status);
        return ResponseEntity.ok(new ApiResponse(200, "Project status updated successfully", "Status: " + status, null));
    }




    @PostMapping
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<UserProjectDTO>> addUserProject(@RequestBody UserProject userProject) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String mentorId = auth.getName();  // Extract mentor ID from authentication
        System.out.println("User Roles: " + auth.getAuthorities());

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


}


