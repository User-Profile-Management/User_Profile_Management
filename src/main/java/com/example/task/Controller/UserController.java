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


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO userDTO) {
        try {

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


            User registeredUser = userService.registerUser(user, userDTO.getRoleName());

            return ResponseEntity.ok("User registered successfully. Awaiting admin approval.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }





    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
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

            if (updatedUser.getContactNo() != null && !updatedUser.getContactNo().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body("Contact Number must be exactly 10 digits.");
            }

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

    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllActiveStudents() {
        List<User> students = userService.getActiveUsersByRole("STUDENT");

        if (students.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(students);
    }


    @GetMapping("/mentors")
    public ResponseEntity<List<User>> getAllActiveMentors() {
        List<User> mentors = userService.getActiveUsersByRole("MENTOR");

        if (mentors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mentors);
    }

    @GetMapping("/mentors/active/count")
    public ResponseEntity<Long> getActiveMentorCount() {
        Long count = userService.getActiveMentorCount();
        return ResponseEntity.ok(count);
    }


    @GetMapping("/students/active/count")
    public ResponseEntity<Long> getActiveStudentCount() {
        Long count = userService.getActiveStudentCount();
        return ResponseEntity.ok(count);
    }






    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingApprovalUsers() {
        return ResponseEntity.ok(userService.getPendingApprovalUsers());
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully (soft delete).");
        } else {
            return ResponseEntity.notFound().build();
        }
    }




    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody RegisterUserDTO.UpdatePasswordRequest request) {
        boolean success = userService.updatePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());

        if (success) {
            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials or password update failed.");
        }
    }
}
