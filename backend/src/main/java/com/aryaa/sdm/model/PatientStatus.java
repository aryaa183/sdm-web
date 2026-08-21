package com.aryaa.sdm.model;

/** Lifecycle state of a patient within a disaster response cycle. */
public enum PatientStatus {
    /** Registered and sitting in the triage queue, not yet processed. */
    WAITING,
    /** Successfully assigned a hospital bed. */
    ADMITTED,
    /** No beds were available in-region or in any connected region. */
    UNASSIGNED
}
