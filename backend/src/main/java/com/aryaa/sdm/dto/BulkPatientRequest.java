package com.aryaa.sdm.dto;

import com.aryaa.sdm.model.Severity;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request body for bulk patient intake — one severity entry per incoming
 * patient. Names/IDs are auto-generated server-side ("Patient_1", "Patient_2", ...),
 * mirroring the original console app's bulk-entry mode.
 */
public record BulkPatientRequest(
        @NotEmpty(message = "Provide at least one severity value") List<Severity> severities
) {
}
