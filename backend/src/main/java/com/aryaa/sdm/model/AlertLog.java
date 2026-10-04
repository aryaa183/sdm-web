package com.aryaa.sdm.model;
import jakarta.persistence.*; import lombok.*; import java.time.Instant;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AlertLog { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String regionName; private int hospitalsNotified; private Instant triggeredAt=Instant.now(); public AlertLog(String regionName,int hospitalsNotified){this.regionName=regionName;this.hospitalsNotified=hospitalsNotified;} }