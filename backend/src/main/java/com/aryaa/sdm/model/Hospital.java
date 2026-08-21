package com.aryaa.sdm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A hospital belonging to a {@link Region}, with a live bed count.
 * {@code totalBeds} is kept alongside {@code bedsAvailable} so the frontend
 * can show occupancy (e.g. "3 / 10 beds free") instead of just a raw count.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int totalBeds;

    private int bedsAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    public Hospital(String name, int totalBeds, Region region) {
        this.name = name;
        this.totalBeds = totalBeds;
        this.bedsAvailable = totalBeds;
        this.region = region;
    }

    public boolean hasFreeBed() {
        return bedsAvailable > 0;
    }

    public void occupyBed() {
        if (!hasFreeBed()) {
            throw new IllegalStateException("No beds available at " + name);
        }
        bedsAvailable--;
    }
}
