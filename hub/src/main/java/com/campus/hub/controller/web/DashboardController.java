// src/main/java/com/campus/hub/controller/web/DashboardController.java
package com.campus.hub.controller.web;

import com.campus.hub.dto.ServiceRequestDTO;
import com.campus.hub.model.ServiceRequest;
import com.campus.hub.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class DashboardController {

    private final ServiceRequestService service;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("pageTitle", "Smart Campus Services Hub");
        return "index";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(
            @RequestParam(defaultValue = "STU2025001") String studentId,
            Model model) {

        List<ServiceRequestDTO> requests = service.getRequestsByStudent(studentId);
        long pendingCount = requests.stream()
                .filter(r -> r.getStatus() == ServiceRequest.RequestStatus.PENDING)
                .count();

        model.addAttribute("studentId", studentId);
        model.addAttribute("requests", requests);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalRequests", requests.size());
        model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());

        return "student/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<ServiceRequestDTO> requests = service.getAllRequests(
                PageRequest.of(page, size));

        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());
        model.addAttribute("statuses", ServiceRequest.RequestStatus.values());

        // Statistics
        model.addAttribute("pendingCount", service.countByStatus(
                ServiceRequest.RequestStatus.PENDING));
        model.addAttribute("totalCount", requests.getTotalElements());

        return "admin/dashboard";
    }

    @GetMapping("/request/{id}")
    public String viewRequest(@PathVariable Long id, Model model) {
        ServiceRequestDTO request = service.getRequestById(id);
        model.addAttribute("request", request);
        model.addAttribute("statuses", ServiceRequest.RequestStatus.values());
        return "request/details";
    }

    @GetMapping("/xml-validator")
    public String xmlValidator(Model model) {
        return "tools/xml-validator";
    }

    @GetMapping("/soap-client")
    public String soapClient(Model model) {
        model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
        return "tools/soap-client";
    }
}