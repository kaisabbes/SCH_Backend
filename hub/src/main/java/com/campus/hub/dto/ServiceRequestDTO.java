package com.campus.hub.dto;

import com.campus.hub.model.ServiceRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestDTO {

    private Long id;

    @NotBlank(message = "Student ID is required")
    @Size(min = 3, max = 20)
    private String studentId;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100)
    private String studentName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Service type is required")
    private ServiceRequest.ServiceType serviceType;

    @NotBlank(message = "Details are required")
    @Size(min = 10, max = 500)
    private String details;

    private ServiceRequest.RequestStatus status;

    @Size(max = 500)
    private String adminNotes;

    @Min(1) @Max(5)
    @Builder.Default
    private Integer priority = 3;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

