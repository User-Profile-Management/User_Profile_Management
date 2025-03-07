package com.example.task.Controller;

import com.example.task.DTO.RegisterUserDTO;
import com.example.task.Entity.User;
import com.example.task.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String greet(){
        return "WELCOME";
    };

    @PostMapping("/register")
<<<<<<< HEAD
    public ResponseEntity<?> registerUser(@RequestBody User user, @RequestParam String roleName) {
        try {
            // Ensure user is saved to database first
            User registeredUser = userService.registerUser(user, roleName);

            // Response message before login
=======
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO userDTO) {
        try {
            // Ensure roleName is provided
            if (userDTO.getRoleName() == null || userDTO.getRoleName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Role is required in the request body");
            }

            // Convert DTO to User entity
            User user = new User();
            user.setGoogleId(userDTO.getGoogleId());
            user.setFullName(userDTO.getFullName());
            user.setEmergencyContact(userDTO.getEmergencyContact());
            user.setDateOfBirth(userDTO.getDateOfBirth());
            user.setPassword(userDTO.getPassword());
            user.setContactNo(userDTO.getContactNo());
            user.setAddress(userDTO.getAddress());
            user.setStatus(User.Status.INACTIVE); // Default status
            user.setProfilePicture(userDTO.getProfilePicture());
            user.setEmail(userDTO.getEmail());

            // Register the user with the role
            User registeredUser = userService.registerUser(user, userDTO.getRoleName());

>>>>>>> 3a184786dd3f0b4132fd4967c5e52ff40c099b75
            return ResponseEntity.ok("User registered successfully. Awaiting admin approval.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }


<<<<<<< HEAD
=======

>>>>>>> 3a184786dd3f0b4132fd4967c5e52ff40c099b75
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        Optional<User> user = userService.loginUser(email, password);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(400).body("Invalid email or password");
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUserProfile(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<?> updateUserByAdmin(@PathVariable String id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUserByAdmin(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingApprovalUsers() {
        return ResponseEntity.ok(userService.getPendingApprovalUsers());
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }


    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String email,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        boolean success = userService.updatePassword(email, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials or password update failed.");
        }
    }
}