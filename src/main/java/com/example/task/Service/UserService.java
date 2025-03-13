package com.example.task.Service;

import com.example.task.DTO.ResponseDTO;
import com.example.task.Entity.Role;
import com.example.task.Entity.User;
import com.example.task.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserBadgeService userBadgeService;


    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, UserBadgeService userBadgeService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userBadgeService = userBadgeService;
    }

    @Transactional
    public User registerUser(User user, String roleName) {
        Role role = roleService.assignRole(roleName);
        user.setRole(role);
        user.setStatus(User.Status.INACTIVE);
        user.setUserId(generateUserId(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public Optional<ResponseDTO> getUserById(String userId) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .map(ResponseDTO::new);
    }

    public List<ResponseDTO> getAllUsers() {
        return userRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(ResponseDTO::new)
                .toList();
    }



    public String generateUserId(Role role) {
        String prefix = "USR";
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
        user.setUserId(generateUserId(user.getRole()));
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);

        return user.filter(u ->
                passwordEncoder.matches(rawPassword, u.getPassword()) &&
                        u.getStatus() == User.Status.ACTIVE
        );
    }


    // Get Pending Approval Users
    public List<User> getPendingApprovalUsers() {
        return userRepository.findByStatus(User.Status.INACTIVE);
    }

    // Get User by Email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }




    // Update User Profile
    public User updateUserProfile(String userId, User updatedUser)
    {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .map(existingUser -> {
                    if (updatedUser.getFullName() != null) {
                        existingUser.setFullName(updatedUser.getFullName());
                    }
                    if (updatedUser.getContactNo() != null) {
                        existingUser.setContactNo(updatedUser.getContactNo());
                    }
                    if (updatedUser.getAddress() != null) {
                        existingUser.setAddress(updatedUser.getAddress());
                    }
                    if (updatedUser.getProfilePicture() != null) {
                        existingUser.setProfilePicture(updatedUser.getProfilePicture());
                    }
                    if (updatedUser.getStatus() != null) {
                        existingUser.setStatus(updatedUser.getStatus());
                    }
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // Update User by Admin
    public User updateUserByAdmin(String userId, User updatedUser) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .map(existingUser -> {
                    existingUser.setFullName(updatedUser.getFullName());
                    existingUser.setContactNo(updatedUser.getContactNo());
                    existingUser.setAddress(updatedUser.getAddress());
                    existingUser.setProfilePicture(updatedUser.getProfilePicture());
                    existingUser.setStatus(updatedUser.getStatus());
                    existingUser.setRole(updatedUser.getRole());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // Soft Delete User (Set deletedAt timestamp instead of deleting)
    @Transactional
    public void deleteUser(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setDeletedAt(LocalDateTime.now()); // Soft delete user
            userRepository.save(user); // Save changes

            // Soft delete all related user badges
            userBadgeService.softDeleteUserBadges(userId);
        });
    }



    // Update Password
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}