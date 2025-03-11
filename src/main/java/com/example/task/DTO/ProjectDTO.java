package com.example.task.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    private Integer id;
    private String projectName;
    private String description;
    private String status;
    private String mentorId; // Store only the ID instead of the whole User entity
}