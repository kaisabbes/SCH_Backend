package com.campus.hub.service;

import com.campus.hub.dto.*;
import com.campus.hub.exception.ResourceNotFoundException;
import com.campus.hub.model.ServiceRequest;
import com.campus.hub.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceRequestService {

    private final ServiceRequestRepository repository;

    public ServiceRequestDTO createRequest(CreateServiceRequestDTO dto) {
        log.info("Creating new service request for student: {}", dto.getStudentId());

        ServiceRequest request = ServiceRequest.builder()
                .studentId(dto.getStudentId())
                .studentName(dto.getStudentName())
                .email(dto.getEmail())
                .serviceType(dto.getServiceType())
                .details(dto.getDetails())
                .priority(dto.getPriority() != null ? dto.getPriority() : 3)
                .status(ServiceRequest.RequestStatus.PENDING)
                .estimatedCompletionDays(calculateEstimatedDays(dto.getServiceType()))
                .build();

        request = repository.save(request);
        log.info("Service request created with ID: {}", request.getId());

        return convertToDTO(request);
    }

    @Transactional(readOnly = true)
    public ServiceRequestDTO getRequestById(Long id) {
        log.debug("Fetching service request with ID: {}", id);

        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service request not found with id: " + id));

        return convertToDTO(request);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestDTO> getRequestsByStudent(String studentId) {
        log.debug("Fetching requests for student: {}", studentId);

        return repository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // FIXED: Changed return type to DTO
    @Transactional(readOnly = true)
    public List<ServiceRequestDTO> getRequestsByStatus(ServiceRequest.RequestStatus status) {
        return repository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ServiceRequestDTO> getAllRequests(Pageable pageable) {
        log.debug("Fetching all service requests with pagination");

        return repository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public ServiceRequestDTO updateRequest(Long id, UpdateServiceRequestDTO dto) {
        log.info("Updating service request with ID: {}", id);

        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service request not found with id: " + id));

        if (dto.getStatus() != null) {
            request.setStatus(dto.getStatus());
            if (dto.getStatus() == ServiceRequest.RequestStatus.COMPLETED) {
                request.setCompletedAt(LocalDateTime.now());
            }
        }

        if (dto.getAdminNotes() != null) {
            request.setAdminNotes(dto.getAdminNotes());
        }

        if (dto.getDetails() != null) {
            request.setDetails(dto.getDetails());
        }

        if (dto.getPriority() != null) {
            request.setPriority(dto.getPriority());
        }

        request = repository.save(request);
        log.info("Service request updated: {}", id);

        return convertToDTO(request);
    }

    public void deleteRequest(Long id) {
        log.info("Deleting service request with ID: {}", id);

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Service request not found with id: " + id);
        }

        repository.deleteById(id);
        log.info("Service request deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestDTO> getPendingRequests() {
        return repository.findByStatus(ServiceRequest.RequestStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long countByStatus(ServiceRequest.RequestStatus status) {
        return repository.countByStatus(status);
    }

    public ServiceRequestDTO updateStatus(Long id, ServiceRequest.RequestStatus status) {
        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service request not found with id: " + id));

        request.setStatus(status);
        if (status == ServiceRequest.RequestStatus.COMPLETED) {
            request.setCompletedAt(LocalDateTime.now());
        }

        request = repository.save(request);
        return convertToDTO(request);
    }

    // Method for SOAP service
    public ServiceRequest createRequestFromSoap(ServiceRequest request) {
        if (request.getPriority() == null) {
            request.setPriority(3);
        }
        if (request.getEstimatedCompletionDays() == null) {
            request.setEstimatedCompletionDays(
                    calculateEstimatedDays(request.getServiceType()));
        }
        if (request.getStatus() == null) {
            request.setStatus(ServiceRequest.RequestStatus.PENDING);
        }

        return repository.save(request);
    }

    private Integer calculateEstimatedDays(ServiceRequest.ServiceType serviceType) {
        return (Integer) switch (serviceType) {
            case CERTIFICATE -> 3;
            case TRANSCRIPT -> 2;
            case ROOM_BOOKING -> 1;
            case EVENT_APPROVAL -> 5;
            case PARKING_PERMIT -> 2;
            case LIBRARY_EXTENSION -> 1;
            case IT_SUPPORT -> 2;
            case FINANCIAL_AID -> 7;
            default -> 3;
        };
    }

    // FIXED: Made public
    public ServiceRequestDTO convertToDTO(ServiceRequest request) {
        return ServiceRequestDTO.builder()
                .id(request.getId())
                .studentId(request.getStudentId())
                .studentName(request.getStudentName())
                .email(request.getEmail())
                .serviceType(request.getServiceType())
                .details(request.getDetails())
                .status(request.getStatus())
                .adminNotes(request.getAdminNotes())
                .priority(request.getPriority())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}