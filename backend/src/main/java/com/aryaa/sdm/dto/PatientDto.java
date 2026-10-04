package com.aryaa.sdm.dto;
import com.aryaa.sdm.model.*; import java.time.Instant;
public record PatientDto(Long id,String name,Severity severity,String preferredHospital,PatientStatus status,String assignedHospitalName,String admittedRegion,Double overflowDistanceKm,Instant dischargedAt){
 public static PatientDto from(Patient p){return from(p,null);} public static PatientDto from(Patient p,Double distance){return new PatientDto(p.getId(),p.getName(),p.getSeverity(),p.getPreferredHospital(),p.getStatus(),p.getAssignedHospital()!=null?p.getAssignedHospital().getName():null,p.getAdmittedRegion(),distance,p.getDischargedAt());}
}