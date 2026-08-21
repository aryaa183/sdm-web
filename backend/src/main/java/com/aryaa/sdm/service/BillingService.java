package com.aryaa.sdm.service;

import com.aryaa.sdm.dto.BillDto;
import com.aryaa.sdm.exception.InvalidOperationException;
import com.aryaa.sdm.exception.ResourceNotFoundException;
import com.aryaa.sdm.model.Bill;
import com.aryaa.sdm.model.Patient;
import com.aryaa.sdm.model.PatientStatus;
import com.aryaa.sdm.repository.BillRepository;
import com.aryaa.sdm.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bill generation — a direct port of {@code generateBill} from the original
 * console app: base cost by severity, plus 18% GST and a 5% service charge.
 * Rates are named constants here instead of inline literals.
 */
@Service
@Transactional
public class BillingService {

    private static final double GST_RATE = 0.18;
    private static final double SERVICE_RATE = 0.05;

    private final PatientRepository patientRepository;
    private final BillRepository billRepository;

    public BillingService(PatientRepository patientRepository, BillRepository billRepository) {
        this.patientRepository = patientRepository;
        this.billRepository = billRepository;
    }

    public BillDto generateBill(Long patientId) {
        return billRepository.findByPatientId(patientId)
                .map(BillDto::from)
                .orElseGet(() -> createBill(patientId));
    }

    private BillDto createBill(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("No patient found with ID " + patientId));

        if (patient.getStatus() != PatientStatus.ADMITTED) {
            throw new InvalidOperationException(
                    "Patient " + patient.getName() + " has not been admitted yet — no bill to generate.");
        }

        double base = patient.getSeverity().getBaseCost();
        double gst = base * GST_RATE;
        double service = base * SERVICE_RATE;

        Bill bill = new Bill(patient, base, gst, service);
        return BillDto.from(billRepository.save(bill));
    }
}
