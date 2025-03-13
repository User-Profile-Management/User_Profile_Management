package com.example.task.Controller;

import com.example.task.DTO.RegisterUserDTO;
import com.example.task.DTO.ResponseDTO;
import com.example.task.Entity.User;
import com.example.task.Repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
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

            return ResponseEntity.ok("User registered successfully. Awaiting admin approval.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody String email, @RequestBody String password) {
        Optional<User> user = userService.loginUser(email, password);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(400).body("Invalid email or password");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<ResponseDTO> users = userService.getAllUsers();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String Userid) {
        Optional<ResponseDTO> user = userService.getUserById(Userid);
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
    public ResponseEntity<ResponseDTO<User>> updateUserByAdmin(@PathVariable String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Update status to ACTIVE
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);

        return ResponseEntity.ok(ResponseDTO.success("User status updated successfully", user));
    }



    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingApprovalUsers() {
        return ResponseEntity.ok(userService.getPendingApprovalUsers());
    }


    // Soft delete implementation
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        Optional<ResponseDTO> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully (soft delete).");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody String email,
                                                 @RequestBody String oldPassword,
                                                 @RequestBody String newPassword) {
        boolean success = userService.updatePassword(email, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials or password update failed.");
        }
    }
}