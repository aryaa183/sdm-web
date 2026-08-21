package com.aryaa.sdm.config;

import com.aryaa.sdm.model.Hospital;
import com.aryaa.sdm.model.Region;
import com.aryaa.sdm.repository.HospitalRepository;
import com.aryaa.sdm.repository.RegionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Seeds the same five-region Uttarakhand network (with the same hospitals and
 * region connections) that was hardcoded as static data in the original
 * console app, so behaviour is identical out of the box. Runs once against
 * the in-memory H2 database on every startup.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final HospitalRepository hospitalRepository;

    public DataSeeder(RegionRepository regionRepository, HospitalRepository hospitalRepository) {
        this.regionRepository = regionRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    public void run(String... args) {
        // Region names, kept 1:1 with the original DisasterManagement static initializer
        List<String> regionNames = List.of("Dehradun", "Haridwar", "Rishikesh", "Nainital", "Chamoli");

        Map<String, Set<String>> connections = Map.of(
                "Dehradun", Set.of("Haridwar", "Rishikesh"),
                "Haridwar", Set.of("Dehradun", "Rishikesh"),
                "Rishikesh", Set.of("Dehradun", "Haridwar"),
                "Nainital", Set.of("Haridwar", "Chamoli"),
                "Chamoli", Set.of("Nainital", "Haridwar")
        );

        Map<String, Region> regions = new LinkedHashMap<>();
        for (String name : regionNames) {
            Region region = new Region(name);
            region.setConnectedRegions(connections.get(name));
            regions.put(name, regionRepository.save(region));
        }

        // (hospital name, bed capacity) pairs per region, matching the original data
        seedHospitals(regions.get("Dehradun"), "Dehradun General", 8, "Dehradun City Hospital", 8);
        seedHospitals(regions.get("Haridwar"), "Haridwar Medical Center", 10, "Haridwar General Hospital", 10);
        seedHospitals(regions.get("Rishikesh"), "Rishikesh Clinic", 6, "Rishikesh Hospital", 10);
        seedHospitals(regions.get("Nainital"), "Nainital Medical Center", 10, "Nainital General Hospital", 8);
        seedHospitals(regions.get("Chamoli"), "Chamoli Medical Center", 10, "Chamoli General Hospital", 8);
    }

    private void seedHospitals(Region region, String name1, int beds1, String name2, int beds2) {
        hospitalRepository.save(new Hospital(name1, beds1, region));
        hospitalRepository.save(new Hospital(name2, beds2, region));
    }
}
