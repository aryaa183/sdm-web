package com.aryaa.sdm.dto;
import com.aryaa.sdm.model.PatientStatus;
import com.aryaa.sdm.model.Severity;
import java.util.List; import java.util.Map;
public record StatsDto(Map<Severity,Long> patientsBySeverity, Map<PatientStatus,Long> patientsByStatus,int totalBeds,int bedsAvailable,double occupancyRate,List<RegionOccupancyDto> regionOccupancy,long totalAlertsTriggered,Map<String,Long> alertsByRegion,List<RecentAlertDto> recentAlerts) {}