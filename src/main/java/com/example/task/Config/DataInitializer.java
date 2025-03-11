package com.example.task.Config;

import com.example.task.Entity.Badge;
import com.example.task.Entity.Role;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final BadgeRepository badgeRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(BadgeRepository badgeRepository, RoleRepository roleRepository) {
        this.badgeRepository = badgeRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        try {
            initializeRoles();
            initializeBadges();
        } catch (IOException e) {
            System.err.println("Error reading badge images: " + e.getMessage());
        }
    }

    private void initializeRoles() {
        createRoleIfNotExists("STUDENT");
        createRoleIfNotExists("EMPLOYEE");
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByRoleName(roleName)) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
            System.out.println("Inserted role: " + roleName);
        }
    }

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
