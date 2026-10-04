package com.aryaa.sdm.model;
import jakarta.persistence.*; import lombok.*; import java.util.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Region {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String name;
 @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="region_connections",joinColumns=@JoinColumn(name="region_id")) @Column(name="connected_region_name") private Set<String> connectedRegions=new HashSet<>();
 private double latitude; private double longitude; private boolean disasterActive;
 public Region(String name){this.name=name;} public Region(String name,double lat,double lon){this.name=name;latitude=lat;longitude=lon;}
}