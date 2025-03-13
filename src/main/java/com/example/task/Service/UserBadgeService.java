package com.example.task.Service;

import com.example.task.DTO.ResponseDTO;
import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Entity.Badge;
import com.example.task.Entity.Project;
import com.example.task.Entity.User;
import com.example.task.Entity.UserBadge;
import com.example.task.Mapper.UserBadgeMapper;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.ProjectRepository;
import com.example.task.Repository.UserBadgeRepository;
import com.example.task.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;


    public UserBadgeService(UserBadgeRepository userBadgeRepository, BadgeRepository badgeRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public ResponseDTO<String> softDeleteUserBadges(String userId) {
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);
        if (userBadges.isEmpty()) {
            return ResponseDTO.error(404, "No badges found for user with ID: " + userId);
        }

            userBadges.forEach(userBadge -> userBadge.setDeletedAt(LocalDateTime.now()));
            userBadgeRepository.saveAll(userBadges);
        return ResponseDTO.success("Badges soft deleted successfully", "Deleted " + userBadges.size() + " badges.");
    }


//    public List<UserBadgeDTO> getUserBadges(String userId) {
//        List<UserBadgeDTO> userBadges = userBadgeRepository.findByUserId(userId)
//                .stream()
//                .map(UserBadgeMapper::mapToUserBadgeDTO)
//                .collect(Collectors.toList());
//
//        if (userBadges.isEmpty()) {
//            throw new RuntimeException("No badges found for user with ID: " + userId);
//        }
//
//        return userBadges;
//    }

    public ResponseDTO<List<UserBadgeDTO>> getUserBadges(String userId) {
        List<UserBadgeDTO> userBadges = userBadgeRepository.findByUserIdAndDeletedAtIsNull(userId) // Only active badges
                .stream()
                .map(UserBadgeMapper::mapToUserBadgeDTO)
                .collect(Collectors.toList());

        if (userBadges.isEmpty()) {
            return ResponseDTO.error(404, "No active badges found for user with ID: " + userId);
        }

        return ResponseDTO.success("User badges retrieved successfully.", userBadges);
    }



    //public List<UserBadgeDTO> getUserBadges(String userId) {
//    return userBadgeRepository.findByUserId(userId)
//            .stream()
//            .map(userBadge -> {
//                UserBadgeDTO dto = new UserBadgeDTO();
//                dto.setUserBadgeId(userBadge.getUserId());
//                dto.setBadgeName(userBadge.getBadge().getName()); // ✅ Corrected
//                dto.setImage( Base64.getEncoder().encodeToString(userBadge.getBadge().getImage()));
//                return dto;
//            })
//            .collect(Collectors.toList());
//}
    @Transactional
    public ResponseDTO<String>  assignBadgeOnProjectCompletion(String userId) {
        int completedCount = projectRepository.countByUserIdAndStatus(userId);
        // Fetch user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Check if the user is ACTIVE
        if (!User.Status.ACTIVE.equals(user.getStatus())) {
            return ResponseDTO.error(403, "User is inactive. Cannot assign badges.");
        }
        String badgeName = switch (completedCount) {
            case 1 -> "Bronze";
            case 2 -> "Silver";
            case 3 -> "Gold";
            default -> "Fallback";
        };

        if (badgeName == null) {
            return ResponseDTO.error(400, "No badge assigned. User has completed " + completedCount + " projects.");
        }

        Badge badge = badgeRepository.findByName(badgeName)
                .orElseThrow(() -> new RuntimeException("Badge not found: " + badgeName));

        UserBadge userBadge = new UserBadge();
        userBadge.setUserId(userId);
        userBadge.setBadge(badge);

        userBadgeRepository.save(userBadge);

        return ResponseDTO.success("Badge assigned successfully", "Assigned " + badgeName + " badge to user " + userId);
    }




}
