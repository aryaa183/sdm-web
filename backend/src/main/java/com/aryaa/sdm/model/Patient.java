package com.aryaa.sdm.model;
import jakarta.persistence.*; import lombok.*; import java.time.Instant;
@Entity @Getter @Setter @NoArgsConstructor
public class Patient {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String name;
 @Enumerated(EnumType.STRING) private Severity severity; private String preferredHospital;
 @Enumerated(EnumType.STRING) private PatientStatus status=PatientStatus.WAITING;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="assigned_hospital_id") private Hospital assignedHospital;
 private String admittedRegion; private Instant registeredAt=Instant.now(); private Instant dischargedAt;
 public Patient(String name,Severity severity,String preferredHospital){this.name=name;this.severity=severity;this.preferredHospital=preferredHospital;}
 public void admitTo(Hospital hospital,String region){assignedHospital=hospital;admittedRegion=region;status=PatientStatus.ADMITTED;}
 public void discharge(){status=PatientStatus.DISCHARGED;dischargedAt=Instant.now();}
}