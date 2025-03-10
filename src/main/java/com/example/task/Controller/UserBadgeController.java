package com.example.task.Controller;

import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Service.UserBadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/badges")
public class UserBadgeController {
    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @PostMapping("/userbadges")
    public ResponseEntity<String> assignBadge(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        return ResponseEntity.ok(userBadgeService.assignBadgeOnProjectCompletion(userId));
    }



    @GetMapping("/{userId}")
    public ResponseEntity<List<UserBadgeDTO>> getUserBadges(@PathVariable String userId) {
        return ResponseEntity.ok(userBadgeService.getUserBadges(userId));
    }

}
