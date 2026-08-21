package com.aryaa.sdm.controller;

import com.aryaa.sdm.dto.AlertResponse;
import com.aryaa.sdm.dto.RegionDto;
import com.aryaa.sdm.service.RegionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    /** GET /api/regions — all regions with their hospitals and connections, for the region picker. */
    @GetMapping
    public List<RegionDto> getAllRegions() {
        return regionService.getAllRegions();
    }

    /** POST /api/regions/{name}/alert — notify hospitals in this region and every connected region. */
    @PostMapping("/{name}/alert")
    public AlertResponse alertRegion(@PathVariable String name) {
        return regionService.alertRegion(name);
    }
}
