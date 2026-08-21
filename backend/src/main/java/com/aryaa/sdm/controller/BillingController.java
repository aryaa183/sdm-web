package com.aryaa.sdm.controller;

import com.aryaa.sdm.dto.BillDto;
import com.aryaa.sdm.service.BillingService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients/{patientId}/bill")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    /** POST /api/patients/{patientId}/bill — generate (or fetch, if already generated) this patient's bill. */
    @PostMapping
    public BillDto generateBill(@PathVariable Long patientId) {
        return billingService.generateBill(patientId);
    }
}
