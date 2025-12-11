package com.campus.hub.controller.rest;

import com.campus.hub.dto.CreateServiceRequestDTO;
import com.campus.hub.dto.ServiceRequestDTO;
import com.campus.hub.dto.UpdateServiceRequestDTO;
import com.campus.hub.model.ServiceRequest;
import com.campus.hub.service.ServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/service-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceRequestController {

    private final ServiceRequestService service;

    @PostMapping
    public ResponseEntity<ServiceRequestDTO> createRequest(
            @Valid @RequestBody CreateServiceRequestDTO requestDTO) {

        ServiceRequestDTO created = service.createRequest(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestDTO> getRequestById(@PathVariable Long id) {
        ServiceRequestDTO request = service.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(sortDirection, sort));

        Page<ServiceRequestDTO> requestsPage = service.getAllRequests(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", requestsPage.getContent());
        response.put("currentPage", requestsPage.getNumber());
        response.put("totalItems", requestsPage.getTotalElements());
        response.put("totalPages", requestsPage.getTotalPages());
        response.put("pageSize", requestsPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByStudent(
            @PathVariable String studentId) {

        List<ServiceRequestDTO> requests = service.getRequestsByStudent(studentId);
        return ResponseEntity.ok(requests);
    }

    // FIXED: Simplified - now returns DTOs directly
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByStatus(
            @PathVariable ServiceRequest.RequestStatus status) {

        List<ServiceRequestDTO> dtos = service.getRequestsByStatus(status);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ServiceRequestDTO>> getPendingRequests() {
        List<ServiceRequestDTO> requests = service.getPendingRequests();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceRequestDTO> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRequestDTO requestDTO) {

        ServiceRequestDTO updated = service.updateRequest(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceRequestDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam ServiceRequest.RequestStatus status) {

        ServiceRequestDTO updated = service.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        for (ServiceRequest.RequestStatus status : ServiceRequest.RequestStatus.values()) {
            stats.put(status.name().toLowerCase(), service.countByStatus(status));
        }

        return ResponseEntity.ok(stats);
    }
}