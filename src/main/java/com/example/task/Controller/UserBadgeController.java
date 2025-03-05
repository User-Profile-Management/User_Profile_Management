package com.example.task.Controller;

import com.example.task.Entity.UserBadge;
import com.example.task.Service.UserBadgeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class UserBadgeController {
    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @PostMapping("/badges") //change the api according to documentation
    public String assignBadge(@RequestParam String userId, @RequestParam int projectId) {
        return userBadgeService.assignBadgeOnProjectCompletion(userId, projectId);
    }

    @GetMapping("/{userId}")
    public List<UserBadge> getUserBadges(@PathVariable String userId) {
        return userBadgeService.getUserBadge(userId);
    }
}
