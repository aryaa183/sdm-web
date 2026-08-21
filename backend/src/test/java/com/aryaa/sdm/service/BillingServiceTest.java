package com.aryaa.sdm.service;

import com.aryaa.sdm.dto.BillDto;
import com.aryaa.sdm.exception.InvalidOperationException;
import com.aryaa.sdm.exception.ResourceNotFoundException;
import com.aryaa.sdm.model.Bill;
import com.aryaa.sdm.model.Hospital;
import com.aryaa.sdm.model.Patient;
import com.aryaa.sdm.model.Region;
import com.aryaa.sdm.model.Severity;
import com.aryaa.sdm.repository.BillRepository;
import com.aryaa.sdm.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillingService billingService;

    @Test
    void calculatesGstAndServiceFeeOnTopOfBaseCost() {
        Patient patient = admittedPatient(1L, Severity.HIGH);

        when(billRepository.findByPatientId(1L)).thenReturn(Optional.empty());
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(billRepository.save(any(Bill.class))).thenAnswer(inv -> inv.getArgument(0));

        BillDto bill = billingService.generateBill(1L);

        // HIGH = ₹2000 base, 18% GST = ₹360, 5% service fee = ₹100 → total ₹2460
        assertThat(bill.baseCost()).isEqualTo(2000.0);
        assertThat(bill.gst()).isCloseTo(360.0, within(0.01));
        assertThat(bill.serviceFee()).isCloseTo(100.0, within(0.01));
        assertThat(bill.total()).isCloseTo(2460.0, within(0.01));
    }

    @Test
    void refusesToBillAPatientWhoWasNeverAdmitted() {
        Patient waitingPatient = new Patient("Kiran", Severity.LOW, null); // status defaults to WAITING
        waitingPatient.setId(2L);

        when(billRepository.findByPatientId(2L)).thenReturn(Optional.empty());
        when(patientRepository.findById(2L)).thenReturn(Optional.of(waitingPatient));

        assertThatThrownBy(() -> billingService.generateBill(2L))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("not been admitted");
    }

    @Test
    void throwsWhenPatientDoesNotExist() {
        when(billRepository.findByPatientId(99L)).thenReturn(Optional.empty());
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billingService.generateBill(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private Patient admittedPatient(Long id, Severity severity) {
        Patient patient = new Patient("Priya", severity, null);
        patient.setId(id);
        Region region = new Region("Dehradun");
        Hospital hospital = new Hospital("Dehradun General", 5, region);
        patient.admitTo(hospital, "Dehradun");
        return patient;
    }
}
