package com.example.task.Service;

import com.example.task.Entity.Badge;
import com.example.task.Entity.User;
import com.example.task.Entity.UserBadge;
import com.example.task.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

    public BadgeService(BadgeRepository badgeRepository, UserBadgeRepository userBadgeRepository,
                        UserRepository userRepository, UserProjectRepository userProjectRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.userRepository = userRepository;
        this.userProjectRepository = userProjectRepository;
    }

    public void saveBadge(String name, MultipartFile file) throws IOException {
        Badge badge = new Badge();
        badge.setName(name);
        if (file != null && !file.isEmpty()) {
            badge.setImage(file.getBytes());
        } else {
            System.out.println("File is null or empty!");
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