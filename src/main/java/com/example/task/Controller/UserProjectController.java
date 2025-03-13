package com.example.task.Controller;

import com.example.task.DTO.ResponseDTO;
import com.example.task.Entity.UserProject;
import com.example.task.Service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @GetMapping //for reference only
    public List<UserProject> getAllUserProjects() {
        return userProjectService.getAllUserProjects();
    }

    @GetMapping("/user/{userId}") //needed
    public List<UserProject> getUserProjectsByUserId(@PathVariable String userId) {
        return userProjectService.getUserProjectsByUserId(userId);
    }


//    @GetMapping("/project/{projectId}") //needed if mentor wants to see all the students in a project
//    public List<UserProject> getUsersByProjectId(@PathVariable int projectId) {
//        return userProjectService.getUsersByProjectId(projectId);
//    }


    @PostMapping // Only mentors
    public ResponseEntity<ResponseDTO<UserProject>> addUserProject(@RequestBody UserProject userProject) {
        try {
            ResponseDTO<UserProject> response = userProjectService.addUserProject(userProject);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.error(500, "Internal Server Error: " + e.getMessage()));
        }
    }


}


//    @DeleteMapping("/{id}") // do soft delete
//    public void deleteUserProject(@PathVariable int id) {
//        userProjectService.deleteUserProject(id);
//    }
//}
