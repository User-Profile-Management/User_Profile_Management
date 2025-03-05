package com.example.task.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserBadgeDTO {
    private int userBadgeId;
    private int userId;
    private int projectId;
    private int badgeId;
}
