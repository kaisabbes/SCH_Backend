package com.campus.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartCampusApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartCampusApplication.class, args);
        System.out.println("✅ Smart Campus Hub started at http://localhost:8080");
        System.out.println("✅ REST API: http://localhost:8080/api/v1/service-requests");
        System.out.println("✅ SOAP WSDL: http://localhost:8080/ws/campusService.wsdl");
    }
}