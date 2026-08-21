package com.aryaa.sdm.dto;

import java.util.List;

/** Outcome of running admission for a disaster region: who got a bed and who didn't. */
public record AdmissionResult(
        String disasterRegion,
        List<PatientDto> admitted,
        List<PatientDto> unassigned
) {
}
