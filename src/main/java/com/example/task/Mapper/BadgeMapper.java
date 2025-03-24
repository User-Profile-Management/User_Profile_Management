package com.example.task.Mapper;

import com.example.task.DTO.BadgeDTO;
import com.example.task.Entity.Badge;

import java.util.Base64;

public class BadgeMapper {
    public static BadgeDTO mapToBadgeDTO(Badge badge) {
        return new BadgeDTO(
                badge.getId(),
                badge.getName(),
                (badge.getImage() != null) ? Base64.getEncoder().encodeToString(badge.getImage()) : null
        );
    }

    public static Badge mapToBadge(BadgeDTO badgeDTO) {
        Badge badge = new Badge();
        badge.setId(badgeDTO.getId());
        badge.setName(badgeDTO.getName());
        if (badgeDTO.getImage() != null) {
            badge.setImage(Base64.getDecoder().decode(badgeDTO.getImage()));
        }
        return badge;
    }
}