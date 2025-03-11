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
@RequestMapping("/api/badges")
public class BadgeController {
    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addBadge(
            @RequestParam("name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            badgeService.saveBadge(name, file);
            return ResponseEntity.ok("Badge added successfully!");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload image.");
        }
    }


    @GetMapping
    public List<Badge> getAllBadges() {
        return badgeService.getAllBadges();
    }




}