package com.aryaa.sdm.dto;
import com.aryaa.sdm.model.AlertLog; import java.time.Instant;
public record RecentAlertDto(String regionName,int hospitalsNotified,Instant triggeredAt) { public static RecentAlertDto from(AlertLog log){ return new RecentAlertDto(log.getRegionName(),log.getHospitalsNotified(),log.getTriggeredAt()); }}