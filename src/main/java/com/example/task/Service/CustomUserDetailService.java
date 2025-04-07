package com.example.task.Service;

import com.example.task.Entity.Badge;
import com.example.task.Entity.User;
import com.example.task.Entity.UserBadge;
import com.example.task.Repository.BadgeRepository;
import com.example.task.Repository.UserBadgeRepository;
import com.example.task.Repository.UserProjectRepository;
import com.example.task.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;


    public CustomUserDetailService(UserRepository userRepository,
                                   UserProjectRepository userProjectRepository,
                                   UserBadgeRepository userBadgeRepository,
                                   BadgeRepository badgeRepository) {
        this.userRepository = userRepository;
        this.userProjectRepository = userProjectRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
    }

    @Transactional
    public void checkAndAssignBadge(String userId) {
        int completedCount = userProjectRepository.countByUserIdAndStatus(userId, "COMPLETED");

        String badgeName = switch (completedCount) {
            case 1 -> "Bronze";
            case 2 -> "Silver";
            case 3 -> "Gold";
            default -> null;
        };

        if (badgeName != null && !userBadgeRepository.existsByUserIdAndBadgeName(userId, badgeName)) {
            Optional<Badge> badgeOptional = badgeRepository.findByName(badgeName);

            if (badgeOptional.isPresent()) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUserId(userId);
                userBadge.setBadge(badgeOptional.get());
                userBadgeRepository.save(userBadge);
            } else {
                System.err.println("Badge not found: " + badgeName);
            }
        }
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        System.out.println("Loading user details for: " + email);
        System.out.println("Role ID: " + user.getRole().getRoleId());
        System.out.println("Role Name: " + user.getRole().getRoleName());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRoleName()))
        );
    }
}
