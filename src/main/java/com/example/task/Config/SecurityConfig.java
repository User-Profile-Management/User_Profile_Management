package com.example.task.Config;

import com.example.task.Security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter,@Lazy UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()

                        // Secured endpoints with role-based access
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers("/api/users/update-password").authenticated()

                        // Admin-only endpoints
                        .requestMatchers("/api/users/pending").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/mentors").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/mentors/count").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/students/count").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/delete/*").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/*/status").hasAuthority("ADMIN")

                        // Students endpoint for mentors and admins
                        .requestMatchers("/api/users/students").hasAnyAuthority("MENTOR", "ADMIN")

                        // Project endpoints
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/projects").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/projects/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/projects/*").hasAnyAuthority("MENTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/*").hasAuthority("ADMIN")

                        // Certificate endpoints
                        .requestMatchers(HttpMethod.POST, "/api/user/*/certificates").hasAuthority("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/user/*/certificates").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/user/*/certificates/*").hasAuthority("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/user/*/certificates/*/download").permitAll()

                        //userproject
                        .requestMatchers(HttpMethod.PUT, "/api/user-projects/users/{userId}/projects/*").hasAuthority("MENTOR")
                        .requestMatchers(HttpMethod.POST, "/api/user-projects").hasAuthority("MENTOR")
                        .requestMatchers(HttpMethod.GET, "/api/user-projects/user/*").hasAnyAuthority("ADMIN", "MENTOR")
//                        .requestMatchers(HttpMethod.GET, "/api/user-projects/user").hasAuthority("STUDENT")

                        //userbadges

                                .requestMatchers(HttpMethod.POST, "/api/badges/userbadges").hasAuthority("MENTOR")

                        //All other requests need authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}