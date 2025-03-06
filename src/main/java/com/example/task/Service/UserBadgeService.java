package com.example.task.Service;

import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Entity.Badge;
import com.example.task.Entity.UserBadge;
import com.example.task.Mapper.UserBadgeMapper;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.UserBadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;

    public UserBadgeService(UserBadgeRepository userBadgeRepository, BadgeRepository badgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
    }

    public String assignBadgeOnProjectCompletion(String userId, int projectId) {
        boolean isCompleted = (projectId % 2 == 0);
        if (!isCompleted) {
            return "Project is not completed. No badge assigned.";
        }

        List<UserBadge> completedProjects = userBadgeRepository.findByUserId(userId);
        int completedCount = completedProjects.size() + 1;

        final String badgeName;
        if (completedCount == 1) {
            badgeName = "Bronze";
        } else if (completedCount == 2) {
            badgeName = "Silver";
        } else if (completedCount == 3) {
            badgeName = "Gold";
        } else {
            return "No badge assigned.";
        }

        Badge badge = badgeRepository.findByName(badgeName)
                .orElseThrow(() -> new RuntimeException("Badge not found: " + badgeName));

        UserBadge userBadge = new UserBadge(0, userId, projectId, badge);
        userBadgeRepository.save(userBadge);

        return "Assigned " + badgeName + " badge to user " + userId;
    }

    public List<UserBadgeDTO> getUserBadge(String userId) {
        return userBadgeRepository.findByUserId(userId)
                .stream()
                .map(UserBadgeMapper::mapToUserBadgeDTO)
                .collect(Collectors.toList());
    }
}
