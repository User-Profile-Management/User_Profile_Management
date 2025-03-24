package com.example.task.Controller;

import com.example.task.DTO.ApiResponse;
import com.example.task.DTO.JWTResponse;
import com.example.task.DTO.LoginRequestDTO;
import com.example.task.Entity.User;
import com.example.task.Service.AuthService;
import com.example.task.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.example.task.Entity.User.Status.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {

        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JWTResponse>> login(@RequestBody LoginRequestDTO loginRequest) {
        try {

            Optional<User> userOptional = userService.getUserByEmail(loginRequest.getEmail());

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponse<>(401, "Invalid email or password.", null, "Authentication failed.")
                );
            }

            User user = userOptional.get();

            if (user.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ApiResponse<>(403, "Your account has been deleted.", null, "Soft-deleted user.")
                );
            }


            switch (user.getStatus()) {
                case PENDING:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            new ApiResponse<>(403, "Your account is pending approval.", null, "User status: PENDING.")
                    );
                case REJECTED:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            new ApiResponse<>(403, "Your account has been rejected.", null, "User status: REJECTED.")
                    );
                case INACTIVE:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            new ApiResponse<>(403, "Your account is inactive. Please contact support.", null, "User status: INACTIVE.")
                    );
                case ACTIVE:

                    JWTResponse jwtResponse = authService.authenticateUser(loginRequest);
                    return ResponseEntity.ok(
                            new ApiResponse<>(200, "Login successful.", jwtResponse, null)
                    );
                default:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            new ApiResponse<>(403, "Invalid account status.", null, "Unknown status.")
                    );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(500, "Error during login.", null, e.getMessage())
            );
        }
    }
}