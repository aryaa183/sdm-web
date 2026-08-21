package com.aryaa.sdm.service;

import com.aryaa.sdm.dto.AdmissionResult;
import com.aryaa.sdm.dto.PatientDto;
import com.aryaa.sdm.model.Hospital;
import com.aryaa.sdm.model.Patient;
import com.aryaa.sdm.model.PatientStatus;
import com.aryaa.sdm.model.Region;
import com.aryaa.sdm.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Admits every WAITING patient to a bed, highest severity first — a direct
 * port of {@code admitPatients} from the original console app, which used a
 * {@code PriorityQueue<Patient>} ordered by severity descending.
 *
 * <p>For each patient, this first tries a free bed in the disaster region;
 * if none is free there, it walks the region's connected regions in order
 * and takes the first free bed found. Patients who can't be placed anywhere
 * end up {@link PatientStatus#UNASSIGNED}.</p>
 */
@Service
@Transactional
public class AdmissionService {

    private final PatientRepository patientRepository;
    private final RegionService regionService;

    public AdmissionService(PatientRepository patientRepository, RegionService regionService) {
        this.patientRepository = patientRepository;
        this.regionService = regionService;
    }

    public AdmissionResult admitWaitingPatients(String disasterRegionName) {
        Region disasterRegion = regionService.getRegionOrThrow(disasterRegionName);

        PriorityQueue<Patient> triageQueue = new PriorityQueue<>(
                Comparator.comparing(Patient::getSeverity).reversed()
        );
        triageQueue.addAll(patientRepository.findByStatus(PatientStatus.WAITING));

        List<PatientDto> admitted = new ArrayList<>();
        List<PatientDto> unassigned = new ArrayList<>();

        while (!triageQueue.isEmpty()) {
            Patient patient = triageQueue.poll();
            Optional<AssignedBed> assignment = findBed(disasterRegion);

            if (assignment.isPresent()) {
                AssignedBed bed = assignment.get();
                bed.hospital().occupyBed();
                patient.admitTo(bed.hospital(), bed.regionName());
                admitted.add(PatientDto.from(patient));
            } else {
                patient.setStatus(PatientStatus.UNASSIGNED);
                unassigned.add(PatientDto.from(patient));
            }
        }

        return new AdmissionResult(disasterRegion.getName(), admitted, unassigned);
    }

    /** First free bed in the disaster region, else the first free bed found while scanning connected regions in order. */
    private Optional<AssignedBed> findBed(Region disasterRegion) {
        Optional<Hospital> local = firstFreeBed(disasterRegion.getName());
        if (local.isPresent()) {
            return local.map(h -> new AssignedBed(h, disasterRegion.getName()));
        }

        for (String connectedRegionName : disasterRegion.getConnectedRegions()) {
            Optional<Hospital> overflow = firstFreeBed(connectedRegionName);
            if (overflow.isPresent()) {
                return overflow.map(h -> new AssignedBed(h, connectedRegionName));
            }
        }

        return Optional.empty();
    }

    private Optional<Hospital> firstFreeBed(String regionName) {
        return regionService.getHospitals(regionName).stream()
                .filter(Hospital::hasFreeBed)
                .findFirst();
    }

    private record AssignedBed(Hospital hospital, String regionName) {
    }
}
