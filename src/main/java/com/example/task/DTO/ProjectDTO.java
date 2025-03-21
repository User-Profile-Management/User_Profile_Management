package com.example.task.DTO;

import com.example.task.Entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    private Integer projectId;
    private String projectName;
    private String description;

    private String mentorId;
    private LocalDateTime deletedAt;


}