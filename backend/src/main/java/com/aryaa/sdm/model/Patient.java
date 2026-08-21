package com.aryaa.sdm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * A patient registered during a disaster response cycle. Starts life in
 * {@link PatientStatus#WAITING} and is transitioned by
 * {@code AdmissionService} once triage/admission runs.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    /** Freeform hospital name the patient (or intake staff) requested. Informational only — admission is bed-driven, not preference-driven, same as the original app. */
    private String preferredHospital;

    @Enumerated(EnumType.STRING)
    private PatientStatus status = PatientStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_hospital_id")
    private Hospital assignedHospital;

    /** Region the patient was actually admitted in — may differ from the disaster region if they overflowed to a connected one. */
    private String admittedRegion;

    private Instant registeredAt = Instant.now();

    public Patient(String name, Severity severity, String preferredHospital) {
        this.name = name;
        this.severity = severity;
        this.preferredHospital = preferredHospital;
    }

    public void admitTo(Hospital hospital, String region) {
        this.assignedHospital = hospital;
        this.admittedRegion = region;
        this.status = PatientStatus.ADMITTED;
    }
}
