package com.campus.hub.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceRequestDTO {

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private com.campus.hub.model.ServiceRequest.ServiceType serviceType;

    @NotBlank @Size(min = 10, max = 500)
    private String details;

    @Min(1) @Max(5)
    private Integer priority;
}