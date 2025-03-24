package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Entity.User;
import com.example.task.Repository.UserRepository;
import com.example.task.Service.UserBadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/badges")
public class UserBadgeController {
    private final UserBadgeService userBadgeService;
    private final UserRepository userRepository;

    public UserBadgeController(UserBadgeService userBadgeService, UserRepository userRepository) {
        this.userBadgeService = userBadgeService;
        this.userRepository = userRepository;
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

    @GetMapping("/userbadges")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiResponse<List<UserBadgeDTO>>> getUserBadges() {

        String userId = getAuthenticatedUserId();

        List<UserBadgeDTO> userBadges = userBadgeService.getUserBadges(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Badges fetched successfully", userBadges, null));
    }

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        String email = authentication.getName();


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));

        return user.getUserId();
    }

}

