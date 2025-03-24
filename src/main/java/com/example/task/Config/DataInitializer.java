package com.example.task.Config;
import com.example.task.Entity.Badge;
import com.example.task.Entity.Role;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final BadgeRepository badgeRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, BadgeRepository badgeRepository) {
        this.roleRepository = roleRepository;
        this.badgeRepository = badgeRepository;
    }

    @PostConstruct
    public void initializeRoles() {
        createRoleIfNotExists("STUDENT");
        createRoleIfNotExists("MENTOR");
        createRoleIfNotExists("ADMIN");
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByRoleName(roleName) == null) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
        }
    }
    @PostConstruct
    @Transactional
    private void initializeBadges() throws IOException {
        List<Badge> defaultBadges = List.of(
                new Badge("Bronze", readImage("src/main/resources/images/Bronze.png")),
                new Badge("Silver", readImage("src/main/resources/images/Silver.png")),
                new Badge("Gold", readImage("src/main/resources/images/Gold.png")),
                new Badge("Fallback", readImage("src/main/resources/images/Fallback.png"))
        );

        for (Badge badge : defaultBadges) {
            if (badgeRepository.findByName(badge.getName()).isEmpty()) {
                badgeRepository.save(badge);
                System.out.println("Inserted badge: " + badge.getName());
            }
        }
    }

    private byte[] readImage(String imagePath) {
        try {
            return Files.readAllBytes(Paths.get(imagePath));
        } catch (IOException e) {
            System.err.println("Image not found at " + imagePath + ". Using fallback.");
            return new byte[0];
        }
    }
}