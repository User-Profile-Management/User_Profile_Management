package com.example.task.Service;

import com.example.task.DTO.JWTResponse;
import com.example.task.DTO.LoginRequestDTO;
import com.example.task.Entity.User;
import com.example.task.Repository.UserRepository;
import com.example.task.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;


import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CustomUserDetailService customUserDetailService;  // ✅ Correctly defined

    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            @Qualifier("userService") UserDetailsService userDetailsService,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            CustomUserDetailService customUserDetailService) {  // ✅ Added to constructor
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.customUserDetailService = customUserDetailService; // ✅ Now properly assigned
    }

    public JWTResponse authenticateUser(LoginRequestDTO loginRequest) throws Exception {
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        } catch (DisabledException e) {
            throw new DisabledException("User account is disabled");
        }


        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("User not found");
        }

        User user = userOptional.get();
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new DisabledException("Your account is pending approval");
        }


        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String token = jwtUtil.generateToken(userDetails);


        customUserDetailService.checkAndAssignBadge(user.getUserId());

        return new JWTResponse(token);
    }

    public JWTResponse generateJwtToken(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return new JWTResponse(token); // ✅ wrap it into JWTResponse
    }

    public String verifyGoogleTokenAndGetEmail(String idToken) throws Exception {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decodedToken.getEmail(); // safely get email from token
        } catch (Exception e) {
            throw new Exception("Invalid Google ID token: " + e.getMessage());
        }
    }
}