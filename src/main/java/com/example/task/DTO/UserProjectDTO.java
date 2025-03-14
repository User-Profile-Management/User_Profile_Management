package com.example.task.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProjectDTO {
    private int id;
    private String userId;
    private int projectId;
    private String status;
}
