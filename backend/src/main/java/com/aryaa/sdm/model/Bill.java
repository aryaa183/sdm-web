package com.aryaa.sdm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Billing record for an admitted patient. GST and service-fee rates are
 * kept as named constants on {@link com.aryaa.sdm.service.BillingService}
 * rather than re-derived here, so there is one source of truth for the math.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    private double baseCost;
    private double gst;
    private double serviceFee;
    private double total;

    private Instant generatedAt = Instant.now();

    public Bill(Patient patient, double baseCost, double gst, double serviceFee) {
        this.patient = patient;
        this.baseCost = baseCost;
        this.gst = gst;
        this.serviceFee = serviceFee;
        this.total = baseCost + gst + serviceFee;
    }
}
