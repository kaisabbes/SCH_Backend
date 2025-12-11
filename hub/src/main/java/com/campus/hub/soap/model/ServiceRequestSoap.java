package com.campus.hub.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServiceRequest", namespace = "http://campus.hub.com/soap")
public class ServiceRequestSoap {

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String studentId;

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String studentName;

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String email;

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String serviceType;

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String details;

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private Integer priority;

    // Add getters and setters if Lombok isn't working
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}