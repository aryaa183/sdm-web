package com.aryaa.sdm.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * A disaster-response region (e.g. a district). Regions know which other
 * regions they're connected to, so admission can overflow patients across
 * a connection when the local region runs out of beds — mirrors
 * {@code regionConnections} from the original console app.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /** Names of regions reachable from this one for overflow admission. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "region_connections", joinColumns = @JoinColumn(name = "region_id"))
    @Column(name = "connected_region_name")
    private Set<String> connectedRegions = new HashSet<>();

    public Region(String name) {
        this.name = name;
    }
}
