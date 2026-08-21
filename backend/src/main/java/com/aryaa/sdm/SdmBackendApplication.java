package com.aryaa.sdm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Smart Disaster Management System backend.
 *
 * <p>This service exposes a REST API that powers the region alert, patient
 * triage/admission, and billing workflows originally written as a console
 * application. See {@code com.aryaa.sdm.service} for the ported business logic.</p>
 */
@SpringBootApplication
public class SdmBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SdmBackendApplication.class, args);
    }
}
