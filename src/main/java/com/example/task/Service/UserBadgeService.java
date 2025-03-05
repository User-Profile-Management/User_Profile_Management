package com.example.task.Service;

import com.example.task.Entity.Badge;
import com.example.task.Entity.UserBadge;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.UserBadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    //change this according to the project code
//    private final ProjectRepository projectRepository;

    public UserBadgeService(UserBadgeRepository userBadgeRepository, BadgeRepository badgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
//        this.projectRepository = projectRepository;
    }

    public String assignBadgeOnProjectCompletion(String userId, int projectId) {


        // change according to the project codes
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
//
//        if (!"Completed".equalsIgnoreCase(project.getStatus())) {
//            return "Project is not completed. No badge assigned.";
//        }

        //FOR TESTING
        boolean isCompleted = (projectId % 2 == 0);

        if (!isCompleted) {
            return "Project is not completed. No badge assigned.";
        }

        // Count completed projects for the user
        List<UserBadge> completedProjects = userBadgeRepository.findByUserId(userId);
        int completedCount = completedProjects.size() + 1; // Including this project


        String name = null;
        if (completedCount == 1) {
            name = "Bronze";
        } else if (completedCount == 2) {
            name = "Silver";
        } else if (completedCount == 3) {
           name = "Gold";
        }

        if (name != null) {
            final String badgeName = name;
            Badge badge = badgeRepository.findByName(badgeName)
                    .orElseThrow(() -> new RuntimeException("Badge not found:"+ badgeName));

            System.out.println(" Fetching badge: " + badge.getId() + " - " + badge.getName());
            System.out.println(" Assigning badge to User: " + userId + ", Project: " + projectId);


            // Assign badge to user
            UserBadge userBadge = new UserBadge(0, userId, projectId, badge);
            userBadgeRepository.save(userBadge);

            return "Assigned " + name + " badge to user " + userId;
        }

        return "No badge assigned.";
    }

    public List<UserBadge> getUserBadge(String userId) {
        return userBadgeRepository.findByUserId(userId);
    }
}
