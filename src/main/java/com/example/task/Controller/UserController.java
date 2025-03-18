package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.RegisterUserDTO;
import com.example.task.Entity.User;
import com.example.task.Service.UserService;
import com.example.task.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody RegisterUserDTO userDTO) {
        try {
            // Validate required fields
            if (userDTO.getFullName() == null || userDTO.getFullName().trim().isEmpty()) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Full Name is required.", null, "Full Name is required.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Email is required.", null, "Email is required.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Password is required.", null, "Password is required.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            // Validate password length (minimum 8 characters)
            if (userDTO.getPassword().length() < 8) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Password must be at least 8 characters long.", null, "Password length is insufficient.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            if (userDTO.getContactNo() == null || userDTO.getContactNo().trim().isEmpty()) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Contact Number is required.", null, "Contact Number is required.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (!userDTO.getContactNo().matches("\\d{10}")) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Contact Number must be exactly 10 digits.", null, "Invalid Contact Number format.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            if (userDTO.getDateOfBirth() == null) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Date of Birth is required.", null, "Date of Birth is required.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userDTO.getAddress() == null || userDTO.getAddress().trim().isEmpty()) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Address is required.", null, "Address is required.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            if (userDTO.getRoleName() == null || userDTO.getRoleName().trim().isEmpty()) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Role is required.", null, "Role is required.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate emergency contact if provided
            if (userDTO.getEmergencyContact() != null &&
                    !userDTO.getEmergencyContact().trim().isEmpty() &&
                    !userDTO.getEmergencyContact().matches("\\d{10}")) {
                ApiResponse<String> errorResponse = new ApiResponse<>(400, "Emergency Contact Number must be exactly 10 digits.", null, "Invalid Emergency Contact format.");
                return ResponseEntity.badRequest().body(errorResponse);
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

            ApiResponse<User> response = new ApiResponse<>(200, "User registered successfully. Awaiting admin approval.", registeredUser, null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = new ApiResponse<>(500, "Error registering user: " + e.getMessage(), null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
                ApiResponse<String> errorResponse = new ApiResponse<>(404, "User not found", null, "User with the given ID does not exist.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }


            User user = currentUserOpt.get();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = new ApiResponse<>(500, "Error retrieving profile", null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    // Secured for ADMIN only
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsersForAdmin() {
        List<User> users = userService.getAllUsers();

        if (users == null || users.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>(204, "No users found", null, "No content available");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        ApiResponse<List<User>> response = new ApiResponse<>(200, "Users fetched successfully", users, null);
        return ResponseEntity.ok(response);
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
    @PreAuthorize("hasAnyAuthority('MENTOR', 'STUDENT)")
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
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable String userId, @RequestParam User updatedUser) {
        try {
            if (updatedUser.getStatus() == null) {
                return ResponseEntity.badRequest().body("Status is required.");
            }

            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            user.setStatus(updatedUser.getStatus());
            user.setUpdatedAt(LocalDateTime.now());

            userService.saveUser(user); // Save the updated user status

            return ResponseEntity.ok("User status updated successfully.");
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