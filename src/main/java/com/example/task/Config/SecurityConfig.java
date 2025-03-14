package com.example.task.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/roles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        .requestMatchers("/api/roles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user-projects/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user-projects").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user-projects/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/projects/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/projects/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/projects/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/projects/user/{userId}/project/{projectId}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/projects/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/admin/update/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/*/certificates").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/*/certificates").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/*/certificates/*/download").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/badges/userbadges").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/badges/userbadges/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/badges").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/user-projects/users/**/projects/**").hasRole("MENTOR")


                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)); // Stateless sessions for APIs

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
