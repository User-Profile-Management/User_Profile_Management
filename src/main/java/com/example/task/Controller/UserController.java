package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.ProfileDTO;
import com.example.task.DTO.RegisterUserDTO;
import com.example.task.Entity.User;
import com.example.task.Service.UserService;
import com.example.task.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            if (userDTO.getFullName() == null || userDTO.getFullName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Full Name is required.", null, "Full Name is required."));
            }
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Email is required.", null, "Email is required."));
            }
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Password is required.", null, "Password is required."));
            }
            if (userDTO.getPassword().length() < 8) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Password must be at least 8 characters long.", null, "Password length is insufficient."));
            }
            if (userDTO.getContactNo() == null || !userDTO.getContactNo().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Contact Number must be exactly 10 digits.", null, "Invalid Contact Number format."));
            }
            if (userDTO.getDateOfBirth() == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Date of Birth is required.", null, "Date of Birth is required."));
            }
            if (userDTO.getAddress() == null || userDTO.getAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Address is required.", null, "Address is required."));
            }
            if (userDTO.getRoleName() == null || userDTO.getRoleName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Role is required.", null, "Role is required."));
            }
            if (userDTO.getEmergencyContact() != null && !userDTO.getEmergencyContact().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Emergency Contact Number must be exactly 10 digits.", null, "Invalid Emergency Contact format."));
            }

            User user = new User();
            user.setFullName(userDTO.getFullName());
            user.setDateOfBirth(userDTO.getDateOfBirth());
            user.setPassword(userDTO.getPassword());
            user.setContactNo(userDTO.getContactNo());
            user.setAddress(userDTO.getAddress());
            user.setStatus(User.Status.PENDING);
            user.setProfilePicture(null);
            user.setEmail(userDTO.getEmail());

            if (userDTO.getEmergencyContact() != null && !userDTO.getEmergencyContact().trim().isEmpty()) {
                user.setEmergencyContact(userDTO.getEmergencyContact());
            }

            User registeredUser = userService.registerUser(user, userDTO.getRoleName());

            return ResponseEntity.ok(new ApiResponse<>(200, "User registered successfully. Awaiting admin approval.", registeredUser, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500, "Error registering user: " + e.getMessage(), null, e.getMessage()));
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/count")
    public ResponseEntity<ApiResponse<Long>> getActiveUserCount(@RequestParam String role) {
        long count;
        switch (role.toUpperCase()) {
            case "MENTOR":
                count = userService.getActiveMentorCount();
                break;
            case "STUDENT":
                count = userService.getActiveStudentCount();
                break;
            default:
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(400, "Invalid role. Use 'MENTOR' or 'STUDENT'", null, "Invalid role parameter")
                );
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Active " + role + " count retrieved successfully", count, null));
    }

    @GetMapping("/users/profile")
    public ResponseEntity<?> getOwnProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();

            Optional<User> currentUserOpt = userService.getUserByEmail(currentUserEmail);

            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "User not found", null, "User does not exist."));
            }

            User user = currentUserOpt.get();
            ProfileDTO profileDTO = new ProfileDTO(
                    user.getUserId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getContactNo(),
                    user.getAddress(),
                    user.getDateOfBirth(),
                    user.getStatus().name(), // Convert Enum to String
                    user.getProfilePicture(),
                    user.getRole().getRoleName() // Assuming Role has getRoleName()
            );

            return ResponseEntity.ok(new ApiResponse<>(200, "Profile retrieved successfully", profileDTO, null));

        } catch (Exception e) {  // ✅ Add this catch block
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error retrieving profile", null, e.getMessage()));
        }
    }




    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MENTOR')")
    public ResponseEntity<ApiResponse<List<ProfileDTO>>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        if (role != null) role = role.toUpperCase();
        if (status != null) status = status.toUpperCase();

        // MENTOR can only view STUDENT profiles
        if (userRole.equals("MENTOR") && (role == null || !role.equals("STUDENT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Forbidden: Mentors can only view students", null, "Access denied"));
        }

        //  Fetch Users from Service
        List<User> users = userService.getUsersByRoleAndStatus(role, status);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(204, "No users found", Collections.emptyList(), null));
        }

        //  Convert List<User> to List<ProfileDTO>
        List<ProfileDTO> profileDTOs = users.stream()
                .map(user -> new ProfileDTO(
                        user.getUserId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getContactNo(),
                        user.getAddress(),
                        user.getDateOfBirth(),
                        user.getStatus().name(), // Convert Enum to String
                        user.getProfilePicture(),
                        user.getRole().getRoleName() // Assuming Role has getRoleName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(200, "Profile retrieved successfully", profileDTOs, null));
    }

    //restore deleted user by the admin
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{userId}/restore")
    public ResponseEntity<ApiResponse<String>> restoreUser(@PathVariable String userId) {
        try {
            userService.restoreUser(userId); // ✅ Uses the correct service method
            return ResponseEntity.ok(new ApiResponse<>(200, "User restored successfully.", null, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null, "User not found"));
        }
    }


    //done
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<String>> updateUserStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status"); // Extract status from JSON

            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(400, "Status is required.", null, "Status is empty or invalid.")
                );
            }

            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // Convert string to enum safely
            try {
                user.setStatus(User.Status.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(400, "Invalid status value.", null, "Invalid status provided.")
                );
            }

            user.setUpdatedAt(LocalDateTime.now());
            userService.saveUser(user);

            return ResponseEntity.ok(new ApiResponse<>(200, "User status updated successfully.", null, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(500, "Error updating user status: " + e.getMessage(), null, e.getMessage())
            );
        }
    }

    @PutMapping("/users/profile")
    @PreAuthorize("hasAnyAuthority('MENTOR', 'STUDENT')")
    public ResponseEntity<?> updateOwnProfile(@RequestBody User updatedUser) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            Optional<User> currentUserOpt = userService.getUserByEmail(currentUserEmail);

            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = userService.updateUserProfile(currentUserOpt.get().getUserId(), updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        try {
            Optional<User> userOptional = userService.getUserById(id);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "User not found with ID: " + id, null, "User not found"));
            }

            User user = userOptional.get();

            if (user.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "User is already deleted.", null, "User already deleted"));
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "User deleted successfully.", null, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error deleting user: " + e.getMessage(), null, e.getMessage()));
        }
    }


    @PutMapping("/users/update-password")
    public ResponseEntity<ApiResponse<String>> updatePassword(@RequestParam String email,
                                                              @RequestParam String oldPassword,
                                                              @RequestParam String newPassword) {
        try {
            boolean success = userService.updatePassword(email, oldPassword, newPassword);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(200, "Password updated successfully.", null, null));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Invalid credentials or password update failed.", null, "Password update failed."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500, "Error updating password", null, e.getMessage()));
        }
    }
}
