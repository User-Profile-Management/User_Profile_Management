package com.example.task.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBadgeDTO {
    private int userBadgeId;
    private String userId;

    private int badgeId;
}
