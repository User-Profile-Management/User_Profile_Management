package com.example.task.Service;

import com.example.task.Entity.Badge;
import com.example.task.Entity.User;
import com.example.task.Entity.UserBadge;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.UserBadgeRepository;
import com.example.task.Repository.UserRepository;
import com.example.task.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public BadgeService(BadgeRepository badgeRepository, UserBadgeRepository userBadgeRepository,
                        UserRepository userRepository, ProjectRepository projectRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public void saveBadge(String name, MultipartFile file) throws IOException {
        Badge badge = new Badge();
        badge.setName(name);
        if (file != null && !file.isEmpty()) {
            badge.setImage(file.getBytes());
        } else {
            System.out.println("File is null or empty!");  // Debugging
        }

        badgeRepository.save(badge);
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Badge getBadgeByName(String name) {
        return badgeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
    }


}