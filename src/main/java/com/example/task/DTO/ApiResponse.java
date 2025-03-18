package com.example.task.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;   // HTTP Status Code (e.g., 200, 201, 400, 404)
    private String message;   // Short description of the response
    private T response;       // Actual response data (Generic Type)
    private String error;     // Error details (null if success)

    public ApiResponse(int i, String projectFound, ProjectDTO project) {
    }
}
