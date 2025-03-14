package com.example.task.Service;

import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Entity.Badge;
import com.example.task.Entity.User;
import com.example.task.Entity.UserBadge;
import com.example.task.Mapper.UserBadgeMapper;
import com.example.task.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;

    public UserBadgeService(UserBadgeRepository userBadgeRepository, BadgeRepository badgeRepository, UserProjectRepository userProjectRepository, UserRepository userRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.userProjectRepository = userProjectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String softDeleteUserBadges(String userId) {
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);
        if (userBadges.isEmpty()) {
            return "No badges found for user with ID: " + userId;
        }

        // Set deleted timestamp for all badges
        userBadges.forEach(userBadge -> userBadge.setDeletedAt(LocalDateTime.now()));
        userBadgeRepository.saveAll(userBadges);

        // Return success response with the count of deleted badges as a String
        return "Deleted " + userBadges.size() + " badges.";
    }

    public List<UserBadgeDTO> getUserBadges(String userId) {
        List<UserBadgeDTO> userBadges = userBadgeRepository.findByUserIdAndDeletedAtIsNull(userId) // Only active badges
                .stream()
                .map(UserBadgeMapper::mapToUserBadgeDTO)
                .collect(Collectors.toList());

        if (userBadges.isEmpty()) {
            throw new RuntimeException("No active badges found for user with ID: " + userId);
        }

        return userBadges;
    }

    @Transactional
    public String assignBadgeOnProjectCompletion(String userId) {
        int completedCount = userProjectRepository.countByUserIdAndStatus(userId, "COMPLETED");

        // Fetch user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Check if the user is ACTIVE
        if (!User.Status.ACTIVE.equals(user.getStatus())) {
            return "User is inactive. Cannot assign badges.";
        }

        String badgeName = switch (completedCount) {
            case 1 -> "Bronze";
            case 2 -> "Silver";
            case 3 -> "Gold";
            default -> "Fallback";
        };

        if (badgeName == null) {
            return "No badge assigned. User has completed " + completedCount + " projects.";
        }

        Badge badge = badgeRepository.findByName(badgeName)
                .orElseThrow(() -> new RuntimeException("Badge not found: " + badgeName));

        UserBadge userBadge = new UserBadge();
        userBadge.setUserId(userId);
        userBadge.setBadge(badge);

        userBadgeRepository.save(userBadge);

        return "Assigned " + badgeName + " badge to user " + userId;
    }
}
