package com.aryaa.sdm.dto;

import com.aryaa.sdm.model.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Request body for manual patient intake ("Enter patient name / severity / preferred hospital"). */
public record PatientRequest(
        @NotBlank(message = "Patient name is required") String name,
        @NotNull(message = "Severity is required") Severity severity,
        String preferredHospital
) {
}
