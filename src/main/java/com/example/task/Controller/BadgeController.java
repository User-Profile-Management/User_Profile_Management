package com.example.task.Controller;

import com.example.task.Entity.Badge;
import com.example.task.Service.BadgeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/users")
public class BadgeController {
    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    // Get all badges
    @GetMapping
    public List<Badge> getAllBadges() {
        return badgeService.getAllBadges();
    }

    // Get a badge by ID(change )
    @GetMapping("/{name}")
    public Badge getBadgeByName(@PathVariable String name) {
        return badgeService.getBadgeByName(name);
    }

    // Upload an image to a badge(chang PK to id)
    @PostMapping(value = "/{name}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadBadgeImage(@PathVariable String name, @RequestParam MultipartFile file) {
        try {
            return ResponseEntity.ok(badgeService.uploadBadgeImage(name, file));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload image");
        }
    }
}
