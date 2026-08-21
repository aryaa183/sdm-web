package com.aryaa.sdm.controller;

import com.aryaa.sdm.dto.AdmissionResult;
import com.aryaa.sdm.dto.BulkPatientRequest;
import com.aryaa.sdm.dto.PatientDto;
import com.aryaa.sdm.dto.PatientRequest;
import com.aryaa.sdm.model.PatientStatus;
import com.aryaa.sdm.repository.PatientRepository;
import com.aryaa.sdm.service.AdmissionService;
import com.aryaa.sdm.service.PatientIntakeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientIntakeService intakeService;
    private final AdmissionService admissionService;
    private final PatientRepository patientRepository;

    public PatientController(PatientIntakeService intakeService,
                              AdmissionService admissionService,
                              PatientRepository patientRepository) {
        this.intakeService = intakeService;
        this.admissionService = admissionService;
        this.patientRepository = patientRepository;
    }

    /** GET /api/patients?status=WAITING — list patients, optionally filtered by status. */
    @GetMapping
    public List<PatientDto> getPatients(@RequestParam(required = false) PatientStatus status) {
        var patients = status != null ? patientRepository.findByStatus(status) : patientRepository.findAll();
        return patients.stream().map(PatientDto::from).toList();
    }

    /** POST /api/patients — register a single patient (manual entry). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto registerPatient(@Valid @RequestBody PatientRequest request) {
        return intakeService.registerPatient(request);
    }

    /** POST /api/patients/bulk — register several patients at once from a list of severities (bulk entry). */
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<PatientDto> registerBulk(@Valid @RequestBody BulkPatientRequest request) {
        return intakeService.registerBulk(request);
    }

    /** POST /api/patients/admit?region=Dehradun — run triage/admission for every WAITING patient. */
    @PostMapping("/admit")
    public AdmissionResult admitPatients(@RequestParam String region) {
        return admissionService.admitWaitingPatients(region);
    }
}
