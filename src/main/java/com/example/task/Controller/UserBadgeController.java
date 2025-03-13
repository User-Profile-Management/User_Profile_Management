package com.example.task.Controller;

import com.example.task.DTO.ResponseDTO;
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
    public ResponseEntity<ResponseDTO<String>> assignBadge(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseDTO.error(400, "User ID is required"));
        }

        ResponseDTO<String> response = userBadgeService.assignBadgeOnProjectCompletion(userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }




    @GetMapping("/userbadges/{userId}") // Added @GetMapping to display in the frontend
    public ResponseEntity<ResponseDTO<List<UserBadgeDTO>>> getUserBadges(@PathVariable String userId) {
        ResponseDTO<List<UserBadgeDTO>> response = userBadgeService.getUserBadges(userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }


}
