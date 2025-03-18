package com.example.task.Controller;

import com.example.task.DTO.RegisterUserDTO;
import com.example.task.Entity.User;
import com.example.task.Service.UserService;
import com.example.task.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO userDTO) {
        try {
            // Validate required fields
            if (userDTO.getFullName() == null || userDTO.getFullName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Full Name is required.");
            }
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required.");
            }
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required.");
            }
            // Validate password length (minimum 8 characters)
            if (userDTO.getPassword().length() < 8) {
                return ResponseEntity.badRequest().body("Password must be at least 8 characters long.");
            }
            if (userDTO.getContactNo() == null || userDTO.getContactNo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Contact Number is required.");
            }

            if (!userDTO.getContactNo().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body("Contact Number must be exactly 10 digits.");
            }
            if (userDTO.getDateOfBirth() == null) {
                return ResponseEntity.badRequest().body("Date of Birth is required.");
            }

            if (userDTO.getAddress() == null || userDTO.getAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Address is required.");
            }
            if (userDTO.getRoleName() == null || userDTO.getRoleName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Role is required.");
            }

            // Validate emergency contact if provided
            if (userDTO.getEmergencyContact() != null &&
                    !userDTO.getEmergencyContact().trim().isEmpty() &&
                    !userDTO.getEmergencyContact().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body("Emergency Contact Number must be exactly 10 digits.");
            }

            // Convert DTO to User entity
            User user = new User();
            user.setFullName(userDTO.getFullName());
            user.setDateOfBirth(userDTO.getDateOfBirth());
            user.setPassword(userDTO.getPassword());
            user.setContactNo(userDTO.getContactNo());
            user.setAddress(userDTO.getAddress());
            user.setStatus(User.Status.INACTIVE); // Default status
            user.setProfilePicture(null);
            user.setEmail(userDTO.getEmail());

            // Set emergency contact if provided
            if (userDTO.getEmergencyContact() != null && !userDTO.getEmergencyContact().trim().isEmpty()) {
                user.setEmergencyContact(userDTO.getEmergencyContact());
            }

            User registeredUser = userService.registerUser(user, userDTO.getRoleName());

            return ResponseEntity.ok("User registered successfully. Awaiting admin approval.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }

    /**
     * Get the current authenticated user's profile details
     * Uses JWT token from the request to identify the user
     */
    @GetMapping("/users/profile")
    public ResponseEntity<?> getOwnProfile() {
        try {
            // Get authenticated user email from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            Optional<User> currentUserOpt = userService.getUserByEmail(currentUserEmail);

            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = currentUserOpt.get();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving profile: " + e.getMessage());
        }
    }

    // Secured for ADMIN only
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsersForAdmin() {
        List<User> users = userService.getAllUsers();

        if (users == null || users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

    // Public or role-based access (Modify if needed)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        if (users == null || users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update the current authenticated user's profile
     * Uses JWT token from the request to identify the user
     */
    @PutMapping("/users/profile")
    public ResponseEntity<?> updateOwnProfile(@RequestBody User updatedUser) {
        try {
            // Get authenticated user ID from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            Optional<User> currentUserOpt = userService.getUserByEmail(currentUserEmail);

            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            String userId = currentUserOpt.get().getUserId();

            // Validate contact number
            if (updatedUser.getContactNo() != null && !updatedUser.getContactNo().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body("Contact Number must be exactly 10 digits.");
            }

            // Validate emergency contact if provided
            if (updatedUser.getEmergencyContact() != null &&
                    !updatedUser.getEmergencyContact().trim().isEmpty() &&
                    !updatedUser.getEmergencyContact().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body("Emergency Contact Number must be exactly 10 digits.");
            }

            User user = userService.updateUserProfile(userId, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Admin only endpoint for updating any user's profile by ID
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserByAdmin(@PathVariable String id, @RequestBody User updatedUser) {
        try {
            // Validate contact numbers
            if (updatedUser.getContactNo() != null && !updatedUser.getContactNo().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body("Contact Number must be exactly 10 digits.");
            }

            if (updatedUser.getEmergencyContact() != null &&
                    !updatedUser.getEmergencyContact().trim().isEmpty() &&
                    !updatedUser.getEmergencyContact().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body("Emergency Contact Number must be exactly 10 digits.");
            }

            User user = userService.updateUserByAdmin(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('MENTOR', 'ADMIN')")
    @GetMapping("/users/students")
    public ResponseEntity<List<User>> getAllActiveStudents() {
        List<User> students = userService.getActiveUsersByRole("STUDENT");

        if (students.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/mentors")
    public ResponseEntity<List<User>> getAllActiveMentors() {
        List<User> mentors = userService.getActiveUsersByRole("MENTOR");

        if (mentors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mentors);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/mentors/count")
    public ResponseEntity<Long> getActiveMentorCount() {
        Long count = userService.getActiveMentorCount();
        return ResponseEntity.ok(count);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/students/count")
    public ResponseEntity<Long> getActiveStudentCount() {
        Long count = userService.getActiveStudentCount();
        return ResponseEntity.ok(count);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/pending")
    public ResponseEntity<List<User>> getPendingApprovalUsers() {
        return ResponseEntity.ok(userService.getPendingApprovalUsers());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully (soft delete).");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/users/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody RegisterUserDTO.UpdatePasswordRequest request) {
        boolean success = userService.updatePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());

        if (success) {
            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials or password update failed.");
        }
    }
}