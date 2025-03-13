package com.example.task.Service;

import com.example.task.DTO.JWTResponse;
import com.example.task.DTO.LoginRequestDTO;
import com.example.task.Entity.User;
import com.example.task.Repository.UserRepository;
import com.example.task.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;


    public JWTResponse authenticateUser(LoginRequestDTO loginRequest) throws Exception {
        try {
            // Directly authenticate, without manually fetching user first
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        } catch (DisabledException e) {
            throw new DisabledException("User account is disabled");
        }

        // Generate token (User is already authenticated, no need to fetch again)
        String token = jwtUtil.generateToken(loginRequest.getEmail());
        return new JWTResponse(token);
    }
}
