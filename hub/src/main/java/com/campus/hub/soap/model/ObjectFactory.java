package com.campus.hub.soap.model;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public ObjectFactory() {}
    public ServiceRequestSoap createServiceRequestSoap() {
        return new ServiceRequestSoap();
    }
    public ServiceResponseSoap createServiceResponseSoap() {
        return new ServiceResponseSoap();
    }
}