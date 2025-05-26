package com.example.kyc.service;

import com.example.kyc.model.OcrResult;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("PassportDataExtractor")
public class PassportDataExtractor implements JavaDelegate {
    private final OcrApiClient ocrApiClient;

    public PassportDataExtractor(OcrApiClient ocrApiClient) {
        this.ocrApiClient = ocrApiClient;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String documentPath = (String) execution.getVariable("passportFilePath");
        OcrResult ocrResult = ocrApiClient.extractData(documentPath);

        // Required fields
        String name = ocrResult.getName();
        String passportNumber = ocrResult.getPassportNumber();
        String nationality = ocrResult.getNationality();
        String dateOfBirth = ocrResult.getDateOfBirth();
        String dateOfIssue = ocrResult.getDateOfIssue();
        String dateOfExpiry = ocrResult.getDateOfExpiry();

        // Optional fields
        String address = ocrResult.getAddress();
        String gender = ocrResult.getGender();
        String placeOfBirth = ocrResult.getPlaceOfBirth();
        String issuingAuthority = ocrResult.getIssuingAuthority();
        String mrz = ocrResult.getMrz();

        // Check required fields
        if (name == null || passportNumber == null || nationality == null ||
            dateOfBirth == null || dateOfIssue == null || dateOfExpiry == null) {
            throw new IllegalStateException("Incomplete required data extracted from passport");
        }

        // Set process variables
        execution.setVariable("customerName", name);
        execution.setVariable("passportNumber", passportNumber);
        execution.setVariable("nationality", nationality);
        execution.setVariable("dateOfBirth", dateOfBirth);
        execution.setVariable("dateOfIssue", dateOfIssue);
        execution.setVariable("dateOfExpiry", dateOfExpiry);
        execution.setVariable("address", address);
        execution.setVariable("gender", gender);
        execution.setVariable("placeOfBirth", placeOfBirth);
        execution.setVariable("issuingAuthority", issuingAuthority);
        execution.setVariable("mrz", mrz);
    }
}
