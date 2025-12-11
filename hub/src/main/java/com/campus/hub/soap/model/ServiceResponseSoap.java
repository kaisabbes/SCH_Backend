package com.campus.hub.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServiceResponse", namespace = "http://campus.hub.com/soap")
public class ServiceResponseSoap {

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String status;

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String message;

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String requestId;

    @XmlElement(namespace = "http://campus.hub.com/soap")
    private String trackingNumber;

    @XmlAttribute
    private String responseDate;

    @XmlAttribute
    private String responseId;

    // Add getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getResponseDate() { return responseDate; }
    public void setResponseDate(String responseDate) { this.responseDate = responseDate; }

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }
}