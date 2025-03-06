package com.example.task.Controller;

import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Service.UserBadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
public class UserBadgeController {
    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @PostMapping("/userbadges")
    public ResponseEntity<String> assignBadge(@RequestBody UserBadgeDTO userBadgeDTO) {
        return ResponseEntity.ok(userBadgeService.assignBadgeOnProjectCompletion(userBadgeDTO.getUserId(), userBadgeDTO.getProjectId()));
    }

    @GetMapping("/{userId}/badges")
    public ResponseEntity<List<UserBadgeDTO>> getUserBadges(@PathVariable String userId) {
        return ResponseEntity.ok(userBadgeService.getUserBadge(userId));
    }
}
