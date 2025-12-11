// src/main/java/com/campus/hub/repository/ServiceRequestRepository.java
package com.campus.hub.repository;

import com.campus.hub.model.ServiceRequest;
import com.campus.hub.model.ServiceRequest.RequestStatus;
import com.campus.hub.model.ServiceRequest.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    // Find by student
    List<ServiceRequest> findByStudentId(String studentId);

    Page<ServiceRequest> findByStudentId(String studentId, Pageable pageable);

    // Find by status
    List<ServiceRequest> findByStatus(RequestStatus status);

    Page<ServiceRequest> findByStatus(RequestStatus status, Pageable pageable);

    // Find by service type
    List<ServiceRequest> findByServiceType(ServiceType serviceType);

    // Complex queries
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.studentId = :studentId AND sr.status IN :statuses")
    List<ServiceRequest> findByStudentAndStatuses(
            @Param("studentId") String studentId,
            @Param("statuses") List<RequestStatus> statuses);

    @Query("SELECT sr FROM ServiceRequest sr WHERE " +
            "(:studentId IS NULL OR sr.studentId = :studentId) AND " +
            "(:status IS NULL OR sr.status = :status) AND " +
            "(:serviceType IS NULL OR sr.serviceType = :serviceType) AND " +
            "(sr.createdAt BETWEEN :startDate AND :endDate)")
    Page<ServiceRequest> searchRequests(
            @Param("studentId") String studentId,
            @Param("status") RequestStatus status,
            @Param("serviceType") ServiceType serviceType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Statistics
    @Query("SELECT COUNT(sr) FROM ServiceRequest sr WHERE sr.status = :status")
    Long countByStatus(@Param("status") RequestStatus status);

    @Query("SELECT sr.serviceType, COUNT(sr) FROM ServiceRequest sr GROUP BY sr.serviceType")
    List<Object[]> countByServiceType();
}