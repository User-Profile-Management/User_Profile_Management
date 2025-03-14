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

    private String mentorId;
}