package com.campus.hub.dto;

import com.campus.hub.model.ServiceRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Update Request DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceRequestDTO {

    private ServiceRequest.RequestStatus status;

    @Size(max = 500)
    private String adminNotes;

    @Size(min = 10, max = 500)
    private String details;

    @Min(1) @Max(5)
    private Integer priority;
}
