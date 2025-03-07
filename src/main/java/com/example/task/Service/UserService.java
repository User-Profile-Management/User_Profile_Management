package com.example.task.Service;

import com.example.task.Entity.Role;
import com.example.task.Entity.User;
import com.example.task.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    // Register User
    @Transactional
    public User registerUser(User user, String roleName) {
        Role role = roleService.assignRole(roleName);
        user.setRole(role);
        return userRepository.save(user);
    }

    // Generate UserID
    public String generateUserId(Role role) {
        String prefix = "USR"; // Default prefix
        if (role != null) {
            switch (role.getRoleName().toUpperCase()) {
                case "STUDENT":
                    prefix = "STU";
                    break;
                case "MENTOR":
                    prefix = "EMP";
                    break;
            }
        }
        // Fetch count of existing users with the same role
        Integer count = userRepository.countByRole(role) + 1;

        // Generate user ID in format STU001, EMP002, etc.
        return String.format("%s%03d", prefix, count);
    }

    @Transactional
    public User createUser(User user) {
        user.setUserId(generateUserId(user.getRole())); // Assign generated ID
        return userRepository.save(user);
    }

    // User Login
    public Optional<User> loginUser(String email, String rawPassword) {
        return userRepository.findByEmail(email);
    }

    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get Pending Approval Users
    public List<User> getPendingApprovalUsers() {
        return userRepository.findByStatus(User.Status.INACTIVE);
    }

    // Get User by Email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Get User by ID
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    // Update User Profile
    public User updateUserProfile(String userId, User updatedUser) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setFullName(updatedUser.getFullName());
                    existingUser.setContactNo(updatedUser.getContactNo());
                    existingUser.setAddress(updatedUser.getAddress());
                    existingUser.setProfilePicture(updatedUser.getProfilePicture());
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // Update User by Admin
    public User updateUserByAdmin(String userId, User updatedUser) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setFullName(updatedUser.getFullName());
                    existingUser.setContactNo(updatedUser.getContactNo());
                    existingUser.setAddress(updatedUser.getAddress());
                    existingUser.setProfilePicture(updatedUser.getProfilePicture());
                    existingUser.setStatus(updatedUser.getStatus());
                    existingUser.setRole(updatedUser.getRole());
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // Delete User
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    // Update Password
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
