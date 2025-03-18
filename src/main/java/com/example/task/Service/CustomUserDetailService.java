package com.example.task.Service;

import com.example.task.Entity.Badge;
import com.example.task.Entity.UserBadge;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.UserBadgeRepository;
import com.example.task.Repository.UserProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService {
    private final UserProjectRepository userProjectRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;

    public CustomUserDetailService(UserProjectRepository userProjectRepository,
                                   UserBadgeRepository userBadgeRepository,
                                   BadgeRepository badgeRepository) {
        this.userProjectRepository = userProjectRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
    }

    @Transactional
    public void checkAndAssignBadge(String userId) {
        int completedCount = userProjectRepository.countByUserIdAndStatus(userId, "COMPLETED");

        String badgeName = switch (completedCount) {
            case 1 -> "Bronze";
            case 2 -> "Silver";
            case 3 -> "Gold";
            default -> null; // No badge if count < 1
        };

        if (badgeName != null && !userBadgeRepository.existsByUserIdAndBadgeName(userId, badgeName)) {
            Badge badge = badgeRepository.findByName(badgeName)
                    .orElseThrow(() -> new RuntimeException("Badge not found: " + badgeName));

            UserBadge userBadge = new UserBadge();
            userBadge.setUserId(userId);
            userBadge.setBadge(badge);
            userBadgeRepository.save(userBadge);
        }
    }
}
