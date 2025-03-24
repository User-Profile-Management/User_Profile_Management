package com.example.task.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProjectDTO {
    private int id;
    private ProfileDTO profile;  // ✅ ProfileDTO matches what is passed in constructor
    private ProjectDTO project;  // ✅ ProjectDTO matches what is passed in constructor
    private String status;
}
