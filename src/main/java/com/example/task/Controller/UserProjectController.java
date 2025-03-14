package com.example.task.Controller;

import com.example.task.Entity.UserProject;
import com.example.task.Service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public List<UserProject> getAllUserProjects() {
        return userProjectService.getAllUserProjects();
    }

    @GetMapping("/user/{userId}") // Needed
    public List<UserProject> getUserProjectsByUserId(@PathVariable String userId) {
        return userProjectService.getUserProjectsByUserId(userId);
    }
    //change the status of a students project
    @PutMapping("/users/{userId}/projects/{projectId}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<String> updateUserProjectStatus(
            @PathVariable String userId,
            @PathVariable Integer projectId,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");
        userProjectService.updateUserProjectStatus(userId, projectId, status);

        return ResponseEntity.ok("Project status updated successfully");
    }

    // @GetMapping("/project/{projectId}") // Needed if mentor wants to see all the students in a project
    // public List<UserProject> getUsersByProjectId(@PathVariable int projectId) {
    //     return userProjectService.getUsersByProjectId(projectId);
    // }

    @PostMapping // Only mentors
    public ResponseEntity<UserProject> addUserProject(@RequestBody UserProject userProject) {
        try {
            UserProject createdUserProject = userProjectService.addUserProject(userProject);
            return ResponseEntity.status(201).body(createdUserProject);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Internal server error response
        }
    }

    // @DeleteMapping("/{id}") // Do soft delete
    // public void deleteUserProject(@PathVariable int id) {
    //     userProjectService.deleteUserProject(id);
    // }
}
