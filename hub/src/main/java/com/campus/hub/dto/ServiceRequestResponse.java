package com.campus.hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ServiceRequestResponse {
    private List<ServiceRequestDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}