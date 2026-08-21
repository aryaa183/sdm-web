package com.aryaa.sdm.repository;

import com.aryaa.sdm.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByPatientId(Long patientId);
}
