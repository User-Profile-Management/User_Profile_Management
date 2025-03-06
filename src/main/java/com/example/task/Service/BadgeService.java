package com.example.task.Service;

import com.example.task.Entity.Badge;
import com.example.task.Repository.BadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public void saveBadge(Badge badge) {
        badgeRepository.save(badge);
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Badge getBadgeByName(String name) {
        return badgeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
    }

    public String uploadBadgeImage(String name, MultipartFile file) throws IOException {
        Badge badge = badgeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Badge not found"));

        badge.setImage(file.getBytes());
        badgeRepository.save(badge);
        return "Image uploaded successfully!";
    }
}
