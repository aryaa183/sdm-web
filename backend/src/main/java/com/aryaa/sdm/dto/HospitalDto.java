package com.aryaa.sdm.dto;

import com.aryaa.sdm.model.Hospital;

public record HospitalDto(
        Long id,
        String name,
        String regionName,
        int totalBeds,
        int bedsAvailable
) {
    public static HospitalDto from(Hospital h) {
        return new HospitalDto(h.getId(), h.getName(), h.getRegion().getName(), h.getTotalBeds(), h.getBedsAvailable());
    }
}
