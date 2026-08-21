package com.aryaa.sdm.dto;

import com.aryaa.sdm.model.Region;

import java.util.List;
import java.util.Set;

public record RegionDto(
        Long id,
        String name,
        Set<String> connectedRegions,
        List<HospitalDto> hospitals
) {
    public static RegionDto from(Region region, List<HospitalDto> hospitals) {
        return new RegionDto(region.getId(), region.getName(), region.getConnectedRegions(), hospitals);
    }
}
