package com.example.task.Service;

import com.example.task.Entity.Role;
import com.example.task.Entity.User;
import com.example.task.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        User user = userOptional.get();

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    @Transactional
    public User registerUser(User user, String roleName) {
        Role role = roleService.assignRole(roleName);
        user.setRole(role);
        user.setProfilePicture(null);
        user.setStatus(User.Status.INACTIVE);
        user.setUserId(generateUserId(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    public String generateUserId(Role role) {
        String prefix = "USR"; // Default prefix
        if (role != null) {
            prefix = switch (role.getRoleName().toUpperCase()) {
                case "STUDENT" -> "STU";
                case "MENTOR" -> "EMP";
                default -> prefix;
            };
        }

        Integer count = userRepository.countByRole(role) + 1;


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
                        u.getStatus() == User.Status.ACTIVE // Check if user is approved
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

    public List<User> getAllUsers() {
        return userRepository.findAllByDeletedAtIsNull(); // Fetch only non-deleted users
    }

    public List<User> getActiveUsersByRole(String roleName) {
        return userRepository.findByRoleAndStatus(roleName, User.Status.ACTIVE);
    }

    public Long getActiveStudentCount() {
        return userRepository.countByRoleAndStatus("STUDENT", User.Status.ACTIVE);

    }



    public Long getActiveMentorCount() {
        return userRepository.countByRoleAndStatus("MENTOR", User.Status.ACTIVE);
    }


    public Optional<User> getUserById(String userId) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId);
    }


    public User updateUserProfile(String userId, User updatedUser) {
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


    @Transactional
    public void deleteUser(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setDeletedAt(LocalDateTime.now()); // Mark as deleted
            userRepository.save(user); // Save changes
        });
    }


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
