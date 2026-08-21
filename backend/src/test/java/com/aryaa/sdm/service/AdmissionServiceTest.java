package com.aryaa.sdm.service;

import com.aryaa.sdm.dto.AdmissionResult;
import com.aryaa.sdm.model.Hospital;
import com.aryaa.sdm.model.Patient;
import com.aryaa.sdm.model.PatientStatus;
import com.aryaa.sdm.model.Region;
import com.aryaa.sdm.model.Severity;
import com.aryaa.sdm.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdmissionServiceTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private RegionService regionService;

    @InjectMocks
    private AdmissionService admissionService;

    private Region dehradun;
    private Hospital dehradunHospital;
    private Hospital haridwarHospital;

    @BeforeEach
    void setUp() {
        dehradun = new Region("Dehradun");
        dehradun.setConnectedRegions(Set.of("Haridwar"));

        dehradunHospital = new Hospital("Dehradun General", 1, dehradun);
        Region haridwar = new Region("Haridwar");
        haridwarHospital = new Hospital("Haridwar Medical Center", 1, haridwar);

        when(regionService.getRegionOrThrow("Dehradun")).thenReturn(dehradun);
    }

    @Test
    void admitsHigherSeverityPatientsFirst() {
        Patient low = new Patient("Amit", Severity.LOW, null);
        Patient high = new Patient("Priya", Severity.HIGH, null);

        when(patientRepository.findByStatus(PatientStatus.WAITING)).thenReturn(List.of(low, high));
        when(regionService.getHospitals("Dehradun")).thenReturn(List.of(dehradunHospital));

        AdmissionResult result = admissionService.admitWaitingPatients("Dehradun");

        // Only one bed available — the HIGH severity patient must win it, not whoever was listed first.
        assertThat(result.admitted()).hasSize(1);
        assertThat(result.admitted().get(0).name()).isEqualTo("Priya");
        assertThat(result.unassigned()).hasSize(1);
        assertThat(result.unassigned().get(0).name()).isEqualTo("Amit");
    }

    @Test
    void overflowsToConnectedRegionWhenLocalBedsAreFull() {
        dehradunHospital.occupyBed(); // local region already full
        Patient patient = new Patient("Rahul", Severity.MEDIUM, null);

        when(patientRepository.findByStatus(PatientStatus.WAITING)).thenReturn(List.of(patient));
        when(regionService.getHospitals("Dehradun")).thenReturn(List.of(dehradunHospital));
        when(regionService.getHospitals("Haridwar")).thenReturn(List.of(haridwarHospital));

        AdmissionResult result = admissionService.admitWaitingPatients("Dehradun");

        assertThat(result.admitted()).hasSize(1);
        assertThat(result.admitted().get(0).assignedHospitalName()).isEqualTo("Haridwar Medical Center");
        assertThat(result.admitted().get(0).admittedRegion()).isEqualTo("Haridwar");
    }

    @Test
    void marksPatientUnassignedWhenNoBedsAnywhere() {
        dehradunHospital.occupyBed();
        haridwarHospital.occupyBed();
        Patient patient = new Patient("Sana", Severity.HIGH, null);

        when(patientRepository.findByStatus(PatientStatus.WAITING)).thenReturn(List.of(patient));
        when(regionService.getHospitals("Dehradun")).thenReturn(List.of(dehradunHospital));
        when(regionService.getHospitals("Haridwar")).thenReturn(List.of(haridwarHospital));

        AdmissionResult result = admissionService.admitWaitingPatients("Dehradun");

        assertThat(result.admitted()).isEmpty();
        assertThat(result.unassigned()).hasSize(1);
        assertThat(patient.getStatus()).isEqualTo(PatientStatus.UNASSIGNED);
    }
}
