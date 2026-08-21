package com.aryaa.sdm.dto;

import com.aryaa.sdm.model.Patient;
import com.aryaa.sdm.model.PatientStatus;
import com.aryaa.sdm.model.Severity;

public record PatientDto(
        Long id,
        String name,
        Severity severity,
        String preferredHospital,
        PatientStatus status,
        String assignedHospitalName,
        String admittedRegion
) {
    public static PatientDto from(Patient p) {
        return new PatientDto(
                p.getId(),
                p.getName(),
                p.getSeverity(),
                p.getPreferredHospital(),
                p.getStatus(),
                p.getAssignedHospital() != null ? p.getAssignedHospital().getName() : null,
                p.getAdmittedRegion()
        );
    }
}
