package com.aryaa.sdm.service;

import com.aryaa.sdm.dto.BulkPatientRequest;
import com.aryaa.sdm.dto.PatientDto;
import com.aryaa.sdm.dto.PatientRequest;
import com.aryaa.sdm.model.Patient;
import com.aryaa.sdm.model.Severity;
import com.aryaa.sdm.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient registration — replaces {@code collectPatients} from the original
 * console app. Manual entry takes a name/severity/preferred hospital; bulk
 * entry auto-names patients ("Patient_1", "Patient_2", ...) from a plain
 * list of severities, same as the original "Bulk Entry" mode.
 */
@Service
@Transactional
public class PatientIntakeService {

    private final PatientRepository patientRepository;

    public PatientIntakeService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientDto registerPatient(PatientRequest request) {
        Patient patient = new Patient(request.name(), request.severity(), request.preferredHospital());
        return PatientDto.from(patientRepository.save(patient));
    }

    public List<PatientDto> registerBulk(BulkPatientRequest request) {
        // Compute the starting sequence number once, then increment locally —
        // querying count() per patient would return the same not-yet-committed
        // value for every entry in this batch.
        long startingOrdinal = patientRepository.count() + 1;
        List<Severity> severities = request.severities();

        List<Patient> patients = new ArrayList<>(severities.size());
        for (int i = 0; i < severities.size(); i++) {
            String name = "Patient_" + (startingOrdinal + i);
            patients.add(new Patient(name, severities.get(i), "AutoAssign"));
        }

        return patientRepository.saveAll(patients).stream().map(PatientDto::from).toList();
    }
}
