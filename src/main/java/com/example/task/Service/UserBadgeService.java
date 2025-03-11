package com.example.task.Service;

import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Entity.Badge;
import com.example.task.Entity.Project;
import com.example.task.Entity.UserBadge;
import com.example.task.Mapper.UserBadgeMapper;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.ProjectRepository;
import com.example.task.Repository.UserBadgeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Base64;
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
//    public List<UserBadgeDTO> getUserBadges(String userId) {
//        return userBadgeRepository.findByUserId(userId)
//                .stream()
//                .map(UserBadgeMapper::mapToUserBadgeDTO)
//                .collect(Collectors.toList());
//    }
public List<UserBadgeDTO> getUserBadges(String userId) {
    return userBadgeRepository.findByUserId(userId)
            .stream()
            .map(userBadge -> {
                UserBadgeDTO dto = new UserBadgeDTO();
                dto.setUserBadgeId(userBadge.getId());
                dto.setBadgeName(userBadge.getBadge().getName()); // ✅ Corrected
                dto.setImage("data:image/png;base64," + Base64.getEncoder().encodeToString(userBadge.getBadge().getImage()));
                return dto;
            })
            .collect(Collectors.toList());
}
    @Transactional
    public String assignBadgeOnProjectCompletion(String userId) {
        int completedCount = projectRepository.countByUserIdAndStatus(userId);

        String badgeName = switch (completedCount) {
            case 1 -> "Bronze";
            case 2 -> "Silver";
            case 3 -> "Gold";
            default -> "Fallback";
        };

        Badge badge = badgeRepository.findByName(badgeName)
                .orElseThrow(() -> new RuntimeException("Badge not found: " + badgeName));

        UserBadge userBadge = new UserBadge();
        userBadge.setUserId(userId);
        userBadge.setBadge(badge);

        userBadgeRepository.save(userBadge);

        return "Assigned " + badgeName + " badge to user " + userId;
    }




}
