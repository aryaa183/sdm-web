package com.aryaa.sdm.repository;

import com.aryaa.sdm.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByRegionNameIgnoreCaseOrderByIdAsc(String regionName);
}
