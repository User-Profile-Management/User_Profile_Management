package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserProject;
import com.example.task.Service.UserProjectService;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


import java.util.List;

@RestController
@RequestMapping("/api/user-projects")
public class UserProjectController {
    private final UserProjectService userProjectService;

    @Autowired
    public UserProjectController(UserProjectService userProjectService) {
        this.userProjectService = userProjectService;
    }

    @GetMapping // For reference only
    public ResponseEntity<ApiResponse<List<UserProject>>> getAllUserProjects() {
        List<UserProject> userProjects = userProjectService.getAllUserProjects();
        return ResponseEntity.ok(new ApiResponse(200, "Fetched all user projects", userProjects,null));
    }

    @GetMapping("/user/{studentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MENTOR') or #studentId == authentication.principal.id")
    public ResponseEntity<ApiResponse> getAssignedProjects(@PathVariable String studentId, Authentication authentication) {
        List<Project> projects = userProjectService.getAssignedProjects(studentId);
        return ResponseEntity.ok(new ApiResponse(200, "Assigned projects fetched successfully", projects,null));
    }

    //change the status of a students project
    @PutMapping("/users/{userId}/projects/{projectId}")
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse> updateUserProjectStatus(
            @PathVariable String userId,
            @PathVariable Integer projectId,
            @RequestParam Map<String, String> request) {

        String status = request.get("status");
        userProjectService.updateUserProjectStatus(userId, projectId, status);

        return ResponseEntity.ok(new ApiResponse(200, "Project status updated successfully", "Status: " + status,null));
    }

    // @GetMapping("/project/{projectId}") // Needed if mentor wants to see all the students in a project
    // public List<UserProject> getUsersByProjectId(@PathVariable int projectId) {
    //     return userProjectService.getUsersByProjectId(projectId);
    // }

    @PostMapping
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<String>> addUserProject(@RequestBody UserProject userProject) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User Roles: " + auth.getAuthorities()); // Debugging line


            UserProject createdUserProject = userProjectService.addUserProject(userProject);
            return ResponseEntity.status(201).body(new ApiResponse(200, "User project added successfully", createdUserProject,null));


        }
    }

