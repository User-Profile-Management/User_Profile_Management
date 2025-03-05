package com.example.task.Mapper;

import com.example.task.DTO.BadgeDTO;
import com.example.task.Entity.Badge;

import java.util.Base64;

public class BadgeMapper {
    public static BadgeDTO maptoBadgesDTO(Badge badge) { //mapping jpa to dto
        return new BadgeDTO(
                badge.getId(),
                badge.getName(),
                (badge.getImage() != null) ? Base64.getEncoder().encodeToString(badge.getImage()) : null
        );
    }
    public static Badge maptoBadges(BadgeDTO badgeDTO){ //mapping dto to jpa
        return new Badge(
                badgeDTO.getId(),
                badgeDTO.getName(),
                (badgeDTO.getImage() != null) ? Base64.getDecoder().decode(badgeDTO.getImage()) : null
        );
    }
}

