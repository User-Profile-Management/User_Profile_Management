package com.example.task.Service;

import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Entity.Badge;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserBadge;
import com.example.task.Mapper.UserBadgeMapper;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.ProjectRepository;
import com.example.task.Repository.UserBadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final ProjectRepository projectRepository;


    public UserBadgeService(UserBadgeRepository userBadgeRepository, BadgeRepository badgeRepository, ProjectRepository projectRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.projectRepository = projectRepository;
    }
    public List<UserBadgeDTO> getUserBadges(String userId) {
        return userBadgeRepository.findByUserId(userId)
                .stream()
                .map(UserBadgeMapper::mapToUserBadgeDTO)
                .collect(Collectors.toList());
    }

    public String assignBadgeOnProjectCompletion(String userId) {
        //Fetch count of completed projects for the user
        int completedCount = projectRepository.countByUserIdAndStatus(userId);

        String badgeName = switch (completedCount) {
            case 1 -> "Bronze";
            case 2 -> "Silver";
            case 3 -> "Gold";
            default -> null;
        };

        if (badgeName == null) {
            return "No new badge assigned.";
        }

        Badge badge = badgeRepository.findByName(badgeName)
                .orElseThrow(() -> new RuntimeException("Badge not found: " + badgeName));

        userBadgeRepository.save(new UserBadge(0, userId, badge));

        return "Assigned " + badgeName + " badge to user " + userId;
    }



}
