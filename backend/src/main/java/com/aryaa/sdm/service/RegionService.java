package com.aryaa.sdm.service;

import com.aryaa.sdm.dto.AlertResponse;
import com.aryaa.sdm.dto.HospitalDto;
import com.aryaa.sdm.dto.RegionDto;
import com.aryaa.sdm.exception.ResourceNotFoundException;
import com.aryaa.sdm.model.Hospital;
import com.aryaa.sdm.model.Region;
import com.aryaa.sdm.repository.HospitalRepository;
import com.aryaa.sdm.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Region lookups and disaster alerting — replaces the free-standing
 * {@code selectRegion} / {@code alertHospitals} functions in the original
 * console app with proper service methods over persisted data.
 */
@Service
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;
    private final HospitalRepository hospitalRepository;

    public RegionService(RegionRepository regionRepository, HospitalRepository hospitalRepository) {
        this.regionRepository = regionRepository;
        this.hospitalRepository = hospitalRepository;
    }

    public List<RegionDto> getAllRegions() {
        return regionRepository.findAll().stream()
                .map(region -> RegionDto.from(region, hospitalDtosFor(region.getName())))
                .toList();
    }

    public Region getRegionOrThrow(String name) {
        return regionRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Unknown region: " + name));
    }

    public List<Hospital> getHospitals(String regionName) {
        return hospitalRepository.findByRegionNameIgnoreCaseOrderByIdAsc(regionName);
    }

    private List<HospitalDto> hospitalDtosFor(String regionName) {
        return getHospitals(regionName).stream().map(HospitalDto::from).toList();
    }

    /**
     * Notifies every hospital in {@code regionName} plus every hospital in
     * each directly connected region — mirrors {@code alertHospitals} from
     * the original app, which printed the same two-tier alert to the console.
     */
    public AlertResponse alertRegion(String regionName) {
        Region region = getRegionOrThrow(regionName);

        List<HospitalDto> local = hospitalDtosFor(region.getName());

        Map<String, List<HospitalDto>> connected = new LinkedHashMap<>();
        for (String connectedRegionName : region.getConnectedRegions()) {
            connected.put(connectedRegionName, hospitalDtosFor(connectedRegionName));
        }

        return new AlertResponse(region.getName(), local, connected);
    }
}
