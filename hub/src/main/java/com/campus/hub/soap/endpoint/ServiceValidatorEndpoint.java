package com.campus.hub.soap.endpoint;

import com.campus.hub.model.ServiceRequest;
import com.campus.hub.service.ServiceRequestService;
import com.campus.hub.soap.model.ServiceRequestSoap;
import com.campus.hub.soap.model.ServiceResponseSoap;
import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.namespace.QName;
import java.time.LocalDateTime;
import java.util.UUID;

@Endpoint
@RequiredArgsConstructor
@Slf4j
public class ServiceValidatorEndpoint {

    private static final String NAMESPACE_URI = "http://campus.hub.com/soap";

    private final ServiceRequestService serviceRequestService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ServiceRequest")
    @ResponsePayload
    public JAXBElement<ServiceResponseSoap> processServiceRequest(
            @RequestPayload JAXBElement<ServiceRequestSoap> request) {

        ServiceResponseSoap response = new ServiceResponseSoap();
        response.setResponseId(UUID.randomUUID().toString());
        response.setResponseDate(LocalDateTime.now().toString());

        try {
            ServiceRequestSoap soapRequest = request.getValue();

            // Simple validation
            if (soapRequest.getStudentId() == null || soapRequest.getStudentId().isEmpty()) {
                response.setStatus("ERROR");
                response.setMessage("Student ID is required");
                return createJAXBResponse(response);
            }

            // Convert to entity
            ServiceRequest entity = convertToEntity(soapRequest);

            // Save using service
            entity = serviceRequestService.createRequestFromSoap(entity);

            // Success response
            response.setStatus("SUCCESS");
            response.setMessage("Service request processed via SOAP");
            response.setRequestId(entity.getId().toString());
            response.setTrackingNumber("TRACK-" + entity.getId());

            log.info("SOAP request processed: {}", entity.getId());

        } catch (Exception e) {
            log.error("SOAP processing error", e);
            response.setStatus("ERROR");
            response.setMessage("Error: " + e.getMessage());
        }

        return createJAXBResponse(response);
    }

    private ServiceRequest convertToEntity(ServiceRequestSoap soapRequest) {
        ServiceRequest.ServiceType serviceType;
        try {
            serviceType = ServiceRequest.ServiceType.valueOf(
                    soapRequest.getServiceType().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            serviceType = ServiceRequest.ServiceType.OTHER;
        }

        // FIXED: Use Integer.valueOf() to avoid int vs Integer issue
        Integer priority = (soapRequest.getPriority() != null) ?
                soapRequest.getPriority() :
                Integer.valueOf(3);

        return ServiceRequest.builder()
                .studentId(soapRequest.getStudentId())
                .studentName(soapRequest.getStudentName() != null ?
                        soapRequest.getStudentName() : "Unknown")
                .email(soapRequest.getEmail() != null ?
                        soapRequest.getEmail() : soapRequest.getStudentId() + "@campus.edu")
                .serviceType(serviceType)
                .details(soapRequest.getDetails() != null ?
                        soapRequest.getDetails() : "SOAP Request")
                .priority(priority)  // Fixed: Now Integer, not int
                .build();
    }

    private JAXBElement<ServiceResponseSoap> createJAXBResponse(ServiceResponseSoap response) {
        return new JAXBElement<>(
                new QName(NAMESPACE_URI, "ServiceResponse"),
                ServiceResponseSoap.class,
                response
        );
    }
}