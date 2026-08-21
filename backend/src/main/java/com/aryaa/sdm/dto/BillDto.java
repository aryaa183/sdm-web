package com.aryaa.sdm.dto;

import com.aryaa.sdm.model.Bill;
import com.aryaa.sdm.model.Severity;

public record BillDto(
        Long patientId,
        String patientName,
        Severity severity,
        double baseCost,
        double gst,
        double serviceFee,
        double total
) {
    public static BillDto from(Bill bill) {
        return new BillDto(
                bill.getPatient().getId(),
                bill.getPatient().getName(),
                bill.getPatient().getSeverity(),
                bill.getBaseCost(),
                bill.getGst(),
                bill.getServiceFee(),
                bill.getTotal()
        );
    }
}
