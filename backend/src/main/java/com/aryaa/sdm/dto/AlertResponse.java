package com.aryaa.sdm.dto;

import java.util.List;
import java.util.Map;

/**
 * Result of alerting a disaster region: the hospitals notified locally, plus
 * the hospitals notified in each connected region (used for overflow later).
 */
public record AlertResponse(
        String region,
        List<HospitalDto> localHospitals,
        Map<String, List<HospitalDto>> connectedRegionHospitals
) {
}
