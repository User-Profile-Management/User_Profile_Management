package com.example.task.Mapper;

import com.example.task.DTO.UserBadgeDTO;
import com.example.task.Entity.UserBadge;

public class UserBadgeMapper {
    public static UserBadgeDTO mapToUserBadgeDTO(UserBadge userBadge) {
        return new UserBadgeDTO(
                userBadge.getId(),
                userBadge.getUserId(),
                userBadge.getBadge().getId()
        );
    }
}
