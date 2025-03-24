package com.example.task.Mapper;

import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Entity.UserBadge;

import java.util.Base64;

public class UserBadgeMapper {
    public static UserBadgeDTO mapToUserBadgeDTO(UserBadge userBadge) {
        return new UserBadgeDTO(
                userBadge.getId(),
                userBadge.getUserId(),
                userBadge.getBadge().getId(),
                userBadge.getBadge().getName(),
                userBadge.getBadge().getImage() != null ? Base64.getEncoder().encodeToString(userBadge.getBadge().getImage()) : null
        );
    }
}

