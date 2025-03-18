package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Service.UserBadgeService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse> assignBadge(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "User ID is required", null,null));
        }
        try {
            String response = userBadgeService.assignBadgeOnProjectCompletion(userId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Badge assigned successfully", response,null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error assigning badge: " + e.getMessage(),null, null));
        }
    }

    @GetMapping("/userbadges/{userId}") // Added @GetMapping to display in the frontend
    public ResponseEntity<ApiResponse> getUserBadges(@PathVariable String userId) {
        List<UserBadgeDTO> userBadges = userBadgeService.getUserBadges(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Badges fetched successfully", userBadges,null));
    }
    // Global Exception Handling for this Controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500, "Internal Server Error: " + ex.getMessage(), null,ex.getMessage()));
    }
}

