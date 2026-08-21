package com.aryaa.sdm.repository;

import com.aryaa.sdm.model.Patient;
import com.aryaa.sdm.model.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByStatus(PatientStatus status);
}
