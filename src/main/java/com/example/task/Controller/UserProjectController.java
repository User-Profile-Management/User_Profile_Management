package com.example.task.Controller;

import com.example.task.Entity.UserProject;
import com.example.task.Service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-projects")
public class UserProjectController {
    private final UserProjectService userProjectService;

    @Autowired
    public UserProjectController(UserProjectService userProjectService) {
        this.userProjectService = userProjectService;
    }


    @GetMapping
    public List<UserProject> getAllUserProjects() {
        return userProjectService.getAllUserProjects();
    }

    @GetMapping("/user/{userId}")
    public List<UserProject> getUserProjectsByUserId(@PathVariable String userId) {
        return userProjectService.getUserProjectsByUserId(userId);
    }


    @GetMapping("/project/{projectId}")
    public List<UserProject> getUsersByProjectId(@PathVariable int projectId) {
        return userProjectService.getUsersByProjectId(projectId);
    }


    @PostMapping
    public UserProject addUserProject(@RequestBody UserProject userProject) {
        return userProjectService.addUserProject(userProject);
    }


    @DeleteMapping("/{id}")
    public void deleteUserProject(@PathVariable int id) {
        userProjectService.deleteUserProject(id);
    }
}
