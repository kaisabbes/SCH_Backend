// src/main/java/com/campus/hub/model/ServiceRequest.java
package com.campus.hub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_requests",
        indexes = {
                @Index(name = "idx_student_id", columnList = "studentId"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_service_type", columnList = "serviceType")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest extends BaseEntity {

    @NotBlank(message = "Student ID is required")
    @Size(min = 3, max = 20)
    @Column(nullable = false)
    private String studentId;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String studentName;

    @NotBlank(message = "Email is required")
    @Column(nullable = false)
    private String email;

    @NotNull(message = "Service type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @NotBlank(message = "Details are required")
    @Size(min = 10, max = 500)
    @Column(nullable = false, length = 500)
    private String details;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Column(length = 500)
    private String adminNotes;

    private LocalDateTime completedAt;

    // File attachment path (for certificates, etc.)
    private String attachmentPath;

    // Priority (1-5, 1 being highest)
    @Min(1)
    @Max(5)
    @Builder.Default
    private Integer priority = 3;

    // Estimated completion days
    private Integer estimatedCompletionDays;

    public enum ServiceType {
        CERTIFICATE("Academic Certificate"),
        TRANSCRIPT("Transcript Request"),
        ROOM_BOOKING("Room Booking"),
        EVENT_APPROVAL("Event Approval"),
        PARKING_PERMIT("Parking Permit"),
        LIBRARY_EXTENSION("Library Extension"),
        IT_SUPPORT("IT Support"),
        FINANCIAL_AID("Financial Aid"),
        OTHER("Other");

        private final String displayName;

        ServiceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RequestStatus {
        PENDING("Pending Review"),
        IN_PROGRESS("In Progress"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String displayName;

        RequestStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}